package dev.braian.habitsre.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.braian.habitsre.R
import dev.braian.habitsre.core.HabitMock
import dev.braian.habitsre.domain.model.Habit
import dev.braian.habitsre.presentation.components.createWavePath
import dev.braian.habitsre.viewmodel.AuthViewModel
import dev.braian.habitsre.viewmodel.HabitViewModel
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    habitViewModel: HabitViewModel,
    navController: NavController?
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val habits by habitViewModel.habitList.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Welcome(authViewModel)
        habitViewModel.getHabits()

        if (habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(colorResource(id = R.color.background_gray))
                    .clip(RoundedCornerShape(16.dp))
                    .align(alignment = Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No habits yet!",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {

            LazyColumn(Modifier.fillMaxSize()) {
                items(habits) { habit ->
                    HabitCard(
                        habit = habit,
                        onClick = {
                            navController?.navigate("HabitDetailScreen/${habit.id}?showBackButton=true")
                        }, onDecrease = {
                            habitViewModel.decreaseProgress(habit)
                        })
                }
            }
        }
    }
}

@Composable
fun Welcome( authViewModel: AuthViewModel? = null) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.welcome), style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 25.sp
            )
        )
        Text(
            text = authViewModel!!.getUserName(), style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun HabitCard(
    habit: Habit?,
    onClick: () -> Unit,
    onDecrease: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProgressCardWithFastMovingWaves( habit, onClick, onDecrease)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProgressCardWithFastMovingWaves(
    habit: Habit? = null,
    onClick: () -> Unit,
    onDecrease: () -> Unit
): Boolean {
    HabitProgressCard(
        habit = habit,
        onClick = onClick,
        onDecrease = onDecrease
    )
    return true
}

@Composable
fun HabitCardContent(
    habit: Habit?,
    days: Long, hours: Long, minutes: Long, onDecrease: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = habit?.name ?: stringResource(R.string.habit),
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%02d:%02d", hours, minutes),
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Days: $days",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onDecrease()
                },
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(
                    text = "-${habit?.difficulty?.timeInMinutes.toString()}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Composable
@Preview
fun HabitCardContentPreview() {
    HabitProgressCard(HabitMock.mockHabit,  onClick = {}, onDecrease = {})
}
@Composable
fun HabitProgressCard(
    habit: Habit?,
    onClick: () -> Unit,
    onDecrease: () -> Unit
) {
    var isShrinking by remember { mutableStateOf(false) }
    val scaleAnimation by animateFloatAsState(
        targetValue = if (isShrinking) 0.95f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    var timeDifference by remember { mutableStateOf(habit?.getTImeDifferenceInMinutes() ?: 0) }
    var progressAnimation by remember { mutableStateOf(0f) }
    var waveOffset by remember { mutableStateOf(0f) }

    // Trigger animation when the decrease button is pressed
    LaunchedEffect(isShrinking) {
        // Animation toggle back to normal size after a short delay
        if (isShrinking) {
            delay(300L)
            isShrinking = false
        }
    }

    LaunchedEffect(habit) {
        timeDifference = habit?.getTImeDifferenceInMinutes() ?: 0L
    }

    LaunchedEffect(Unit) {
        while (true) {
            timeDifference = habit?.getTImeDifferenceInMinutes() ?: 0L
            val minutesInCurrentDay = timeDifference % HabitMock.minutesInDay
            progressAnimation = minutesInCurrentDay.toFloat() / HabitMock.minutesInDay.toFloat()
            delay(60 * 1000)
        }
    }

    // Infinite wave animation
    LaunchedEffect(Unit) {
        while (true) {
            waveOffset += 0.1f
            delay(16L)
        }
    }

    val (days, hours, minutes) = habit!!.calculateTimeComponents(timeDifference)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(120.dp)
            .clickable(onClick = onClick)
            .graphicsLayer(scaleX = scaleAnimation, scaleY = scaleAnimation),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HabitWaveCanvas(progressAnimation, waveOffset, habit)
            HabitCardContent(habit, days, hours, minutes) {
                // Trigger the decrease and update the timeDifference
                onDecrease()
                // Decrease the time difference by a set amount (e.g., 30 minutes)
                timeDifference -= habit?.difficulty?.timeInMinutes ?: 0
                if (timeDifference < 0) timeDifference = 0 // Ensure it doesn't go below zero

                isShrinking = true // Set shrinking to true to animate the scale down
            }
        }
    }
}


@Composable
fun HabitWaveCanvas(
    progressAnimation: Float,
    waveOffset: Float,
    habit: Habit?
) {

    val habitBackgroundColor = colorResource(id = R.color.background_gray)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val progressWidth = canvasWidth * progressAnimation
        val waveAmplitude = 20.dp.toPx()
        val waveFrequency = 200f
        val waveSegments = 50
        val path = createWavePath(
            progressWidth,
            canvasHeight,
            waveAmplitude,
            waveFrequency,
            waveSegments,
            waveOffset
        )

        drawRect(color = habitBackgroundColor)
        drawPath(path = path, color = Color(habit?.backgroundColor!!.toColorInt()))
    }
}