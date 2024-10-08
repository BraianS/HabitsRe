package dev.braian.habitsre.domain.repository

import dev.braian.habitsre.core.Response
import dev.braian.habitsre.domain.model.Habit
import kotlinx.coroutines.flow.Flow


interface HabitRepository {
    suspend fun saveHabit(habit: Habit): Response<Boolean>
    suspend fun updateHabit(habit: Habit): Response<Boolean>
    suspend fun deletedHabitById(uid: String): Response<Boolean>
    suspend fun getHabits(): Flow<List<Habit>>
    fun getHabitById(habitId: String): Flow<Habit?>
    fun decreaseProgress(habit: Habit): Response<Boolean>
}