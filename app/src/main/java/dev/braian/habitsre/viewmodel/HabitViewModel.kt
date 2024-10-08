package dev.braian.habitsre.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.braian.habitsre.core.Response
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

    init {
        loadHabits()
    }

    fun loadHabits(){
        viewModelScope.launch {
            repository.getHabits().collect{ habits ->
                _habitList.value = habits
            }
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

    fun decreaseProgress(habit: Habit) = CoroutineScope(Dispatchers.IO).launch {
        val response = repository.decreaseProgress(habit)

        if(response is Response.Success) {
            loadHabits()
        }
    }

}