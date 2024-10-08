package dev.braian.habitsre.presentation.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.braian.habitsre.core.Constants
import dev.braian.habitsre.core.Screen
import dev.braian.habitsre.presentation.screen.AddHabitScreen
import dev.braian.habitsre.presentation.screen.HabitDetailScreen
import dev.braian.habitsre.presentation.screen.HomeScreen
import dev.braian.habitsre.presentation.screen.ProfileScreen
import dev.braian.habitsre.viewmodel.AuthViewModel
import dev.braian.habitsre.viewmodel.HabitViewModel

@Composable
@ExperimentalMaterial3Api
fun MyBottomAppBar(authViewModel: AuthViewModel, habitViewModel: HabitViewModel) {

    val navigationController = rememberNavController()
    val context = LocalContext.current.applicationContext

    val selected = remember {
        mutableStateOf(Icons.Default.Home)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar() {

                IconButton(onClick = {
                    selected.value = Icons.Default.Home
                    navigationController.navigate(Screen.HomeScreen.name.toString()) {
                        popUpTo(0)
                    }
                }, modifier = Modifier.weight(50f)) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "",
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Home) Color.White else Color.DarkGray
                    )
                }

                IconButton(onClick = {
                    val habitId  = 0
                    selected.value = Icons.Default.Add
                    navigationController.navigate("AddHabitScreen/${habitId}") {
                        popUpTo(0)
                    }
                }, modifier = Modifier.weight(50f)) {
                    Icon(
                        Icons.Default.Add, contentDescription = "", modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Add) Color.White else Color.DarkGray
                    )
                }

                IconButton(onClick = {
                    selected.value = Icons.Default.Person
                    navigationController.navigate(Screen.ProfileScreen.name.toString()) {
                        popUpTo(0)
                    }
                }, modifier = Modifier.weight(50f)) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Person) Color.White else Color.DarkGray
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(onClick = {
                        Toast.makeText(
                            context,
                            "Open Bottom Sheet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "", tint = Color.Black)
                    }
                }

            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navigationController,
            startDestination = Screen.HomeScreen.name,
            modifier = Constants.ScreenConstant.commonModifier.padding(paddingValues)
        ) {
            composable(Screen.ProfileScreen.name) {
                ProfileScreen(
                    modifier = Constants.ScreenConstant.commonModifier.padding(
                        paddingValues
                    ), authViewModel
                )
            }
            composable(
                route = "HabitDetailScreen/{habitId}",
                arguments = listOf(
                    navArgument("habitId") { type = NavType.StringType },
                    navArgument("showBackButton") {
                    type = NavType.BoolType
                    defaultValue = false
                })
            ) {backStackSry ->
                val habitId = backStackSry.arguments?.getString("habitId")
                HabitDetailScreen(
                    habitId = habitId!!,
                    habitViewModel = habitViewModel,
                    onBackClick = { navigationController.popBackStack() },
                    onEditClick = { navigationController.navigate("AddHabitScreen/$habitId")}
                )
            }

            composable(Screen.HomeScreen.name) {
                HomeScreen(
                    modifier = Constants.ScreenConstant.commonModifier.padding(paddingValues),
                    authViewModel,
                    habitViewModel,
                    navController = navigationController
                )
            }

            composable(
                route = "AddHabitScreen/{habitId}",
                arguments = listOf(
                    navArgument("habitId") { type = NavType.StringType },
                    navArgument("showBackButton") {
                        type = NavType.BoolType
                        defaultValue = false
                    })
            ) {backStackSry ->
                val habitId = backStackSry.arguments?.getString("habitId")
               AddHabitScreen(
                   habitId = habitId,
                   authViewModel = authViewModel,
                   habitViewModel = habitViewModel,
                   onBackClick = { navigationController.navigate("HomeScreen") }
               )
            }
        }
    }
}