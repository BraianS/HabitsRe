package dev.braian.habitsre.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.braian.habitsre.core.Constants
import dev.braian.habitsre.domain.model.Habit
import dev.braian.habitsre.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository
): ViewModel() {

    private val _habitList = MutableStateFlow<List<Habit>>(emptyList())
    val habitList: StateFlow<List<Habit>> = _habitList.asStateFlow()


    fun getHabits() = CoroutineScope(Dispatchers.IO).launch {
        repository.getHabits().collect{ habits ->
            Log.i(Constants.TAG.HABIT_VIEW_MODEL, "Get habits...")
            _habitList.value = habits
        }
    }

    fun createHabit(habit:Habit) = CoroutineScope(Dispatchers.IO).launch {
            repository.saveHabit(habit)
    }

    fun getHabitById(habitId: String): Flow<Habit?> {
        return repository.getHabitById(habitId)
    }

    fun updateHabit(habit:Habit) = CoroutineScope(Dispatchers.IO).launch {
         repository.updateHabit(habit)
    }

    fun deleteHabit(habitId:String) = CoroutineScope(Dispatchers.IO).launch {
            repository.deletedHabitById(habitId)
    }

    fun decreaseProgress(habit: Habit) = viewModelScope.launch {
        val response = repository.decreaseProgress(habit)
        getHabits()
    }
    fun resetHabit(habitId:String) = CoroutineScope(Dispatchers.IO).launch {
        repository.resetHabit(habitId)
    }

}