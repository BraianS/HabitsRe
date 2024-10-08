package dev.braian.habitsre


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.braian.habitsre.domain.model.DataProvider
import dev.braian.habitsre.presentation.navigation.MyBottomAppBar
import dev.braian.habitsre.presentation.screen.LoginScreen
import dev.braian.habitsre.ui.theme.HabitsReTheme
import dev.braian.habitsre.viewmodel.AuthViewModel
import dev.braian.habitsre.viewmodel.HabitViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val habitViewModel by viewModels<HabitViewModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            HabitsReTheme {
                 val currentUSer = authViewModel.currentUser.collectAsState().value

                 DataProvider.updateAuthState(currentUSer)

                 if (DataProvider.isAuthenticated) {

                     MyBottomAppBar(authViewModel,habitViewModel )
                 } else {
                     LoginScreen(authViewModel)
                 }
            }
        }
    }
}