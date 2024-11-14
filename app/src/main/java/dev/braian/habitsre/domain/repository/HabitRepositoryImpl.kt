package dev.braian.habitsre.domain.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.braian.habitsre.core.Constants
import dev.braian.habitsre.core.Response
import dev.braian.habitsre.domain.model.Habit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val providesRealtimeDBReference: DatabaseReference,
) : HabitRepository {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    var habits = _habits.asStateFlow()

    override fun getHabitById(habitId: String): Flow<Habit?> = callbackFlow {
        val habitRef = providesRealtimeDBReference.child(habitId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val habit = snapshot.getValue(Habit::class.java)
                    trySend(habit).isSuccess
                } catch (e: Exception) {
                    close(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        habitRef.addValueEventListener(listener)
        awaitClose { habitRef.removeEventListener(listener) }
    }

    override fun decreaseProgress(habit: Habit): Response<Boolean> {
        try {

            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val entityReference = database.child("habit").child(habit.id!!)

            val response = habit.addDecreaseTime(habit.difficulty!!)
            if (response) {
                entityReference.setValue(habit)
                Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Success")
            } else {
                Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Failure")
            }
            return Response.Success(true)

        } catch (e: Exception) {
            return Response.Failure(e)
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Error: ${e.message}")
        }
    }

    override suspend fun saveHabit(habit: Habit): Response<Boolean> {

        try {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val entityReference = database.child("habit").push()
            habit.id = entityReference.key.toString()
            habit.userId = auth.currentUser?.uid
            entityReference.setValue(habit.copy(habit.id))
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Success")
            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Error: ${e.message}")
        }
    }

    override suspend fun updateHabit(habit: Habit): Response<Boolean> {

        try {
            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            val entityReference = database.child("habit").child(habit.id!!)
            entityReference.setValue(habit)
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Success")
            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Error: ${e.message}")
        }
    }

    override suspend fun deletedHabitById(habitId: String): Response<Boolean> {
        try {
            providesRealtimeDBReference.child(habitId).setValue(null)
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Deleted successfully")
            return Response.Success(true)
        } catch (e: Exception) {
            return Response.Failure(e)
            Log.i(Constants.TAG.HABIT_REPOSITORY_IMPL, "Error: ${e.message}")
        }
    }

    override suspend fun getHabits(): Flow<List<Habit>> {
        val currentUser = auth.currentUser
            ?: return flowOf(emptyList()) // Retorna uma lista vazia se não houver usuário logado

        return callbackFlow {
            val habitsList = mutableListOf<Habit>()

            // Listener para ouvir as mudanças em tempo real
            val listener =
                providesRealtimeDBReference.orderByChild("userId").equalTo(currentUser.uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            habitsList.clear() // Limpa a lista para evitar duplicados
                            for (habitSnapshot in snapshot.children) {
                                val habit = habitSnapshot.getValue(Habit::class.java)
                                habit?.let { habitsList.add(it) }
                            }
                            trySend(habitsList.toList()) // Emite a lista atualizada pelo Flow
                            _habits.value = habitsList.toList() // Atualiza o StateFlow
                            Log.i("HabitRepositoryImpl", "Fetched ${habitsList.size} habits")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("HabitRepositoryImpl", "Database error: ${error.message}")
                            close(error.toException()) // Fecha o Flow com a exceção
                        }
                    })

            // Remove o listener quando o Flow é cancelado
            awaitClose {
                providesRealtimeDBReference.removeEventListener(listener)
            }
        }
    }

    override fun resetHabit(habitId: String): Response<Boolean> {
        return try {
            val instant = LocalDateTime.now().toInstant(ZoneOffset.UTC).epochSecond
            val updateMap = mapOf("lastResetAt" to instant)
            providesRealtimeDBReference.child(habitId).updateChildren(updateMap)
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}