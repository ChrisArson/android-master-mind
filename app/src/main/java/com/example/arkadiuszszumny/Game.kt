package com.example.arkadiuszszumny

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun CircularButton(color: Color, onClick: () -> Unit) {
    val animatedColor by animateColorAsState(targetValue = color, animationSpec = tween(durationMillis = 500))

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(animatedColor)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = animatedColor, contentColor = MaterialTheme.colorScheme.onBackground)
    ) {}
}

@Composable
fun SelectableColorsRow(colors: List<Color>, onClick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        colors.forEachIndexed { index, color ->
            CircularButton(color = color) {
                onClick(index)
            }
        }
    }
}

@Composable
fun SmallCircle(color: Color, delayMillis: Int = 0) {
    var currentColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(color) {
        delay(delayMillis.toLong())
        currentColor = color
    }

    val animatedColor by animateColorAsState(targetValue = currentColor, animationSpec = tween(durationMillis = 500))

    Canvas(modifier = Modifier.size(25.dp), onDraw = {
        drawCircle(color = animatedColor)
    })
}

@Composable
fun FeedbackCircles(colors: List<Color>) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        colors.forEachIndexed { index, color ->
            SmallCircle(color = color, delayMillis = index * 200)
        }
    }
}

@Composable
fun GameRow(
    selectedColors: List<Color>,
    feedbackColors: List<Color>,
    clickable: Boolean,
    onSelectColorClick: (Int) -> Unit,
    onCheckClick: () -> Unit
) {
    val allColorsSelected = selectedColors.all { it != Color.Gray }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        SelectableColorsRow(colors = selectedColors, onClick = onSelectColorClick)
        Spacer(modifier = Modifier.size(1.dp))
        Box(modifier = Modifier.width(50.dp)) {
            androidx.compose.animation.AnimatedVisibility(
                visible = allColorsSelected,
                enter = scaleIn(animationSpec = tween(500)) + fadeIn(animationSpec = tween(500)),
                exit = scaleOut(animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
            ) {
                IconButton(
                    onClick = onCheckClick,
                    enabled = clickable,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp),
                    colors = IconButtonDefaults.filledIconButtonColors()
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Check")
                }
            }
        }
        Spacer(modifier = Modifier.size(1.dp))
        FeedbackCircles(colors = feedbackColors)
    }
}

@Composable
fun GameScreen(navController: NavController, colors: Int) {
    val availableColors = remember { listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.Black, Color.White, Color.DarkGray, Color.LightGray).take(colors) }
    var selectedColors by remember { mutableStateOf(List(4) { Color.Gray }) }
    var feedbackColors by remember { mutableStateOf(List(4) { Color.Gray }) }
    var solutionColors by remember { mutableStateOf(selectRandomColors(availableColors)) }
    var attempts by remember { mutableStateOf(0) }
    var gameWon by remember { mutableStateOf(false) }
    var pastAttempts by remember { mutableStateOf(listOf<List<Color>>()) }
    var pastFeedback by remember { mutableStateOf(listOf<List<Color>>()) }

    fun onSelectColorClick(index: Int) {
        selectedColors = selectNextAvailableColor(availableColors, selectedColors, index)
    }

    fun onCheckClick() {
        feedbackColors = checkColors(selectedColors, solutionColors, Color.Gray)
        attempts++
        pastAttempts = pastAttempts + listOf(selectedColors)
        pastFeedback = pastFeedback + listOf(feedbackColors)
        if (feedbackColors.all { it == Color.Red }) {
            gameWon = true
            navController.navigate("results/$attempts")
        } else {
            selectedColors = List(4) { Color.Gray }
            feedbackColors = List(4) { Color.Gray }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Attempts: $attempts")
        LazyColumn {
            items(pastAttempts.zip(pastFeedback)) { (attempt, feedback) ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = attempt) {
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = expandVertically(animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
                ) {
                    GameRow(
                        selectedColors = attempt,
                        feedbackColors = feedback,
                        clickable = false,
                        onSelectColorClick = {},
                        onCheckClick = {}
                    )
                }
            }
        }
        if (!gameWon) {
            GameRow(
                selectedColors = selectedColors,
                feedbackColors = feedbackColors,
                clickable = true,
                onSelectColorClick = ::onSelectColorClick,
                onCheckClick = ::onCheckClick
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.popBackStack("login", inclusive = false)
        }) {
            Text(text = "Logout")
        }
    }
}

fun selectRandomColors(availableColors: List<Color>): List<Color> {
    return availableColors.shuffled().take(4)
}

fun selectNextAvailableColor(
    availableColors: List<Color>,
    selectedColors: List<Color>,
    index: Int
): List<Color> {
    val nextColor = availableColors[(availableColors.indexOf(selectedColors[index]) + 1) % availableColors.size]
    return selectedColors.toMutableList().apply { this[index] = nextColor }
}

fun checkColors(selectedColors: List<Color>, solutionColors: List<Color>, defaultColor: Color): List<Color> {
    return selectedColors.mapIndexed { index, color ->
        when {
            color == solutionColors[index] -> Color.Red
            color in solutionColors -> Color.Yellow
            else -> defaultColor
        }
    }
}
