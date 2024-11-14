package dev.braian.habitsre.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.braian.habitsre.presentation.components.TimeDifferenceDisplayWithCanvasCircle
import dev.braian.habitsre.viewmodel.HabitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitDetailScreen(
    habitId: String,
    habitViewModel: HabitViewModel,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val habit by habitViewModel.getHabitById(habitId).collectAsState(initial = null)

    habit?.let {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = it.name!!, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(2.dp))

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val epochTimeInSeconds = it.createdAt!!
                    val dateInMillis = epochTimeInSeconds * 1000
                    val formattedDate = dateFormat.format(Date(dateInMillis))

                    Text(text = "Created at $formattedDate")
                }
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onEditClick(it.id!!) },
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text(text = "Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        habitViewModel.deleteHabit(habit?.id!!)
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text(text = "Delete")
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))
                // Canvas and time display
                TimeDifferenceDisplayWithCanvasCircle(habit)

                // Reset button below the canvas
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        habitViewModel.resetHabit(habit?.id!!)
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reset")
                }
            }
        }
    } ?: run {
        Text(text = "Loading habit details...")
    }
}