package dev.braian.habitsre.core

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.braian.habitsre.domain.model.Habit


enum class Screen {
    HomeScreen,
    AddHabitScreen,
    ProfileScreen
}

object HabitMock {

    val minutesInDay = (24 * 60)

    val mockHabit = Habit(
        "habit1",
        "Morning Workout",
        "#C7253E",
        createdAt = 1726064330,

        )

    val eventTime =  4 * 60 * 60 * 1000L // Progresso atual de 4 horas
}


class Constants private constructor() {

    object Colors {
        val colors = listOf(
            Color(0xFFF48FB1),
            Color(0xFF80CBC4),
            Color(0XFF03DAC5),
            Color(0xFFFFEB3B),
            Color(0xFF8FD6F7),
            Color(0XFFBB86FC),
            Color(0XFF6200EE)
        )

        val CustomGrayColor = Color(0xFFE4E4E5)
    }

    object ScreenConstant {
        val commonModifier = Modifier.padding(top = 10.dp, start = 5.dp, end = 5.dp)
    }

    object TAG {
        const val HABIT_DETAIL_SCREEN = "habitDetailScreen"
        const val ADD_HABIT_SCREEN = "HabitAddScreen"
        const val HOME_SCREEN = "HabitRepositoryImpl"
        const val HABIT_REPOSITORY_IMPL = "HabitRepositoryImpl"
        const val AUTH_REPOSITORY_IMPL = "AuthRepositoryImpl"
        const val GOOGLE_SIGN_IN_REQUEST = "googleSignInRequest"
        const val GOOGLE_SIGN_UP_REQUEST = "googleSignUpRequest"
    }
}

