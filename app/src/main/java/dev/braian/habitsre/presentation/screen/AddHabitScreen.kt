package dev.braian.habitsre.presentation.screen


import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.braian.habitsre.R
import dev.braian.habitsre.core.Constants
import dev.braian.habitsre.domain.model.Difficulty
import dev.braian.habitsre.domain.model.Habit
import dev.braian.habitsre.viewmodel.AuthViewModel
import dev.braian.habitsre.viewmodel.HabitViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun AddHabitScreen(
    habitId: String?,
    authViewModel: AuthViewModel,
    habitViewModel: HabitViewModel,
    onBackClick: () -> Unit
) {
    val habit by habitViewModel.getHabitById(habitId!!).collectAsState(initial = null)

    if (habit != null) {

        addHabitForm(
            authViewModel = authViewModel,
            habitViewModel = habitViewModel,
            onBackClick = onBackClick,
            habit = habit,
            update = true
        )
    } else {
        addHabitForm(
            authViewModel = authViewModel,
            habitViewModel = habitViewModel,
            onBackClick = onBackClick,
            habit = habit,
            update = false
        )
    }

}

@Composable
fun DifficultyRadioButtonList(
    selectedDifficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Difficulty.values().forEach { difficulty ->
            Row(
                Modifier
                    .selectableGroup()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedDifficulty == difficulty,
                    onClick = { onDifficultyChanged(difficulty) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "-${difficulty.timeInMinutes} min - ${difficulty.name} - ${difficulty.description}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addHabitForm(
    authViewModel: AuthViewModel,
    habitViewModel: HabitViewModel,
    onBackClick: () -> Unit,
    habit: Habit?,
    update: Boolean
) {
    val localContext = LocalContext.current

    val habitBackgroundColor = colorResource(id = R.color.background_gray)

    val maxChar = 20

    val coroutineScope = rememberCoroutineScope()

    val habitUpdated = mutableStateOf(Habit())

    val habitName = rememberSaveable {
        if (update) mutableStateOf(habit?.name!!) else mutableStateOf("")
    }

    var hexColorBackground = remember {
        mutableStateOf(habit?.backgroundColor ?: "")
    }
    val selectedDifficulty = remember {

        mutableStateOf(if (update) habit?.difficulty ?: Difficulty.Normal else Difficulty.Normal)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = if (update) "Update the Habit" else "Add the Habit",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,

                        ),
                    modifier = Modifier.padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (update) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = habitName.value!!,
                onValueChange = { if (it.length <= maxChar) habitName.value = it },
                placeholder = { Text(text = "name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = habitBackgroundColor
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Select the minutes of every fail will decrease your progress to help you evolve")

            Spacer(modifier = Modifier.height(6.dp))

            //Select Difficulty
            DifficultyRadioButtonList(
                selectedDifficulty = selectedDifficulty.value,
                onDifficultyChanged = { selectedDifficulty.value = it }
            )

            //Select Color
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {


                hexColorBackground = ColourButton(
                    Constants.Colors.colors, onColorSelected = {}, update,
                    initialColor = Color(
                        android.graphics.Color.parseColor(
                            habit?.backgroundColor ?: "#FFF48FB1"
                        )
                    )
                )
            }

            //Save Button
            Button(
                onClick = {
                    if (TextUtils.isEmpty(habitName.value)) {
                        Toast.makeText(localContext, "Please enter habit name", Toast.LENGTH_SHORT)
                            .show()
                    } else {

                        coroutineScope.launch {

                            val instant = LocalDateTime.now().toInstant(ZoneOffset.UTC)

                            if (update) {
                                habit?.saveHabit(
                                    name = habitName.value,
                                    backgroundColor = hexColorBackground.value,
                                    selectedDifficulty.value,
                                    authViewModel.currentUser.value?.uid,
                                    habit.createdAt
                                )

                                habitViewModel.updateHabit(habit!!)

                                Toast.makeText(
                                    localContext,
                                    "Habit Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackClick()
                            } else {
                                habitUpdated.value.saveHabit(
                                    name = habitName.value,
                                    backgroundColor = hexColorBackground.value,
                                    selectedDifficulty.value,
                                    authViewModel.currentUser.value?.uid,
                                    instant.epochSecond
                                )
                                println(habitUpdated.toString())
                                habitViewModel.createHabit(habitUpdated.value)
                                onBackClick()
                                Toast.makeText(
                                    localContext,
                                    "Habit created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    }

                }, colors = ButtonDefaults.buttonColors(
                    Color.White,
                    contentColor = Color.Black,

                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (update) "Update" else "Save")
            }

            if (update) {
                Button(
                    onClick = {
                        habitViewModel.deleteHabit(habit?.id!!)
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Delete")
                }
            }
        }
    }

}


@Composable
fun ColourButton(
    colors: List<Color>,
    onColorSelected: (Color) -> Unit,
    update: Boolean = false,
    initialColor: Color = colors[0]
): MutableState<String> {

    var colorPickerOpen by rememberSaveable { mutableStateOf(false) }
//    var currentlySelected by rememberSaveable(saver = colourSaver()) { mutableStateOf(colors[0]) }
    var currentlySelected by rememberSaveable(saver = colourSaver()) {
        mutableStateOf(if (update) initialColor else colors[0])
    }

    Box(
        modifier = Modifier
            .padding(top = 12.dp, start = 3.dp, end = 3.dp, bottom = 3.dp)
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(20))
            .border(
                1.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                RoundedCornerShape(20)
            )
            .clickable {
                colorPickerOpen = true
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select colour",
            )
            Canvas(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(20))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        RoundedCornerShape(20)
                    )
                    .background(currentlySelected)
                    .clickable {
                        colorPickerOpen = true
                    }
            ) {}
        }
    }

    var hexStringSelected = remember {
        mutableStateOf(initialColor.toHexString())
    }

    if (colorPickerOpen) {
        ColorDialog(
            colorList = colors,
            onDismiss = { colorPickerOpen = false },
            currentlySelected = currentlySelected,
            onColorSelected = {
                currentlySelected = it
                onColorSelected(it)
                hexStringSelected.value = it.toHexString()
            }
        )

    }

    return hexStringSelected

}

@Composable
private fun ColorDialog(
    colorList: List<Color>,
    onDismiss: (() -> Unit),
    currentlySelected: Color,
    onColorSelected: ((Color) -> Unit) // when the save button is clicked
) {
    val gridState = rememberLazyGridState()

    AlertDialog(
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.outline,
        onDismissRequest = onDismiss,
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = gridState
            ) {
                items(colorList) { color ->
                    var borderWidth = 0.dp
                    if (currentlySelected == color) {
                        borderWidth = 2.dp
                    }

                    Canvas(modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            borderWidth,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                            RoundedCornerShape(20.dp)
                        )
                        .background(color)
                        .requiredSize(70.dp)
                        .clickable {
                            onColorSelected(color)
                            onDismiss()
                        }
                    ) {
                    }
                }
            }
        },
        confirmButton = {}
    )
}

fun colourSaver() = Saver<MutableState<Color>, String>(
    save = { state -> state.value.toHexString() },
    restore = { value -> mutableStateOf(value.toColor()) }
)

fun Color.toHexString(): String {
    return String.format("#%06X", (0xFFFFFF and this.toArgb()))
}


fun String.toColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}