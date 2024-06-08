package com.example.arkadiuszszumny

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(navController: NavController) {
    val name = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val colors = rememberSaveable { mutableStateOf("") }
    val profileImageUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { selectedUri ->
            profileImageUri.value = selectedUri
        }
    )

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MasterAnd",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier
                .padding(bottom = 48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
        )

        ProfileImageWithPicker(profileImageUri.value) {
            imagePicker.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextFieldWithError(
            valueState = name,
            label = "Name",
            keyboardType = KeyboardType.Text,
            isValid = { it.isNotEmpty() },
            errorMessage = "Name can't be empty"
        )

        OutlinedTextFieldWithError(
            valueState = email,
            label = "Email",
            keyboardType = KeyboardType.Email,
            isValid = { it.contains("@") && it.contains(".") },
            errorMessage = "Invalid email address"
        )

        OutlinedTextFieldWithError(
            valueState = colors,
            label = "Number of colors",
            keyboardType = KeyboardType.Number,
            isValid = { it.toIntOrNull() in 6..10 },
            errorMessage = "Must be between 6 and 10"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val numberOfColors = colors.value.toIntOrNull()
            if (numberOfColors != null && numberOfColors in 6..10) {
                navController.navigate("game/$numberOfColors")
            }
        }) {
            Text("Next")
        }
    }
}

@Composable
fun OutlinedTextFieldWithError(
    valueState: MutableState<String>,
    label: String,
    keyboardType: KeyboardType,
    isValid: (String) -> Boolean,
    errorMessage: String
) {
    val isError = remember { derivedStateOf { !isValid(valueState.value) } }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valueState.value,
            onValueChange = { valueState.value = it },
            label = { Text(label) },
            singleLine = true,
            isError = isError.value,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError.value) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ProfileImageWithPicker(
    profileImageUri: Uri?,
    onPickImage: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
    ) {
        if (profileImageUri == null) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_question_mark_24),
                contentDescription = "Profile photo",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = profileImageUri,
                contentDescription = "Profile photo",
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(onClick = onPickImage, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                contentDescription = "Edit photo"
            )
        }
    }
}