package dev.braian.habitsre.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.braian.habitsre.domain.model.Habit
import dev.braian.habitsre.presentation.screen.toColor
import kotlinx.coroutines.delay

@Composable
fun TimeDifferenceDisplayWithCanvasCircle(habit: Habit? = null) {

    var timeDifference by remember {
        mutableStateOf(0L)
    }

    var days by remember {
        mutableStateOf(0L)
    }

    val totalMinutesInDay = 24 * 60
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            timeDifference = habit?.getTImeDifferenceInMinutes() ?: 0L


            days = timeDifference / totalMinutesInDay
            val minutesInCurrentDay = timeDifference % totalMinutesInDay
            progress = minutesInCurrentDay.toFloat() / totalMinutesInDay.toFloat()

            delay(60 * 1000L)
        }
    }
    val hours = (timeDifference % totalMinutesInDay) / 60
    val minutes = timeDifference % 60

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center,
    ) {

        Canvas(modifier = Modifier.size(200.dp)) {
            val canvasSize = size.minDimension
            val strokeWidth = 15.dp.toPx()

            drawCircle(
                color = Color.LightGray,
                radius = canvasSize / 2 - strokeWidth / 2,
                style = Stroke(strokeWidth)
            )

            drawArc(
                color = habit?.backgroundColor?.toColor()!!,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Day: $days",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = String.format("%02d:%02d", hours, minutes),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
