package dev.braian.habitsre.domain.model

import androidx.annotation.DrawableRes
import dev.braian.habitsre.R

sealed class OnBoardingPage(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
) {
    object Welcome : OnBoardingPage(
        image = R.drawable.undraw_information_tab_re_f0w3,
        title = "Welcome",
        description = "Welcome to HabitsRe! Your personal habit tracker to help you stay on track and achieve your goals."
    )

    object Presentation : OnBoardingPage(
        image = R.drawable.undraw_mindfulness_8gqa,
        title = "Track Your Habits",
        description = "Our app helps you track your habits. Every time you miss a goal, your progress won’t reset. Instead, time will decrease, allowing you to pick up where you left off."
    )

    object Finish : OnBoardingPage(
        image = R.drawable.undraw_winners_re_wr1l,
        title = "Get Started",
        description = "You’re all set! Start tracking your habits and make continuous improvements every day."
    )

    object Login : OnBoardingPage(
        image = R.drawable.undraw_winners_re_wr1l,
        title = "Login",
        description = "Login Screen."
    )
}
