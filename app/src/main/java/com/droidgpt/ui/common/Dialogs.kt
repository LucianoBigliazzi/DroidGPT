package com.droidgpt.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droidgpt.model.ChatViewModel
import com.droidgpt.ui.theme.DroidGPTTheme
import java.util.Locale


@Composable
fun ClearChatDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Clear chat?") },
        text = { Text("This action cannot be undone") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}


@Preview
@Composable
fun ClearChatDialogPreview(){
    DroidGPTTheme {
        ClearChatDialog(onDismiss = {}) {

        }
    }
}


@Composable
fun TemperatureDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit,
    viewModel: ChatViewModel
){

    var temperatureValue by remember {
        mutableFloatStateOf(0.7f)
    }

    println(temperatureValue)

    val haptic = LocalHapticFeedback.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(temperatureValue)
                onDismiss()
            }) {
            Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Select temperature") },
        text = {
            Column  {

                Text(text = "Higher temperature means higher creativity.")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This action will clear the current chat",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = temperatureValue,
                    onValueChange = {
                        temperatureValue = it
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    steps = 10
                )

                Text(
                    text = String.format(Locale.US ,"%.1f", temperatureValue),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Preview
@Composable
fun TemperatureDialogPreview(){


    DroidGPTTheme {
        TemperatureDialog(
            onDismiss = { /*TODO*/ },
            { },
            viewModel = viewModel()
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChangeApiKeyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
){

    var newKey by remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    val callback = {
        focusManager.clearFocus()
    }



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Change key") },
        text = {

            Column {

                Text(text = "If valid, the provided key will overwrite the current one.")

                Spacer(modifier = Modifier.height(8.dp))

                Row (verticalAlignment = Alignment.CenterVertically) {

                    Box (
                        modifier = Modifier.padding(top = 8.dp, end = 12.dp)
                    ){
                        Icon(Icons.TwoTone.Edit, contentDescription = null)
                    }

                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { value -> newKey = value },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        label = { Text(text = "Enter new key") },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(newKey) }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )

}

@Preview
@Composable
fun ChangeApiKeyDialogPreview(){
    DroidGPTTheme {
        ChangeApiKeyDialog(
            onDismiss = { /*TODO*/ },
            onConfirm = {},
        )
    }
}


@Composable
fun ShowBehaviourDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    currentSysMessage: String
){

    var newBehaviour by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Change behaviour") },
        text = {

            Column {
                Text(
                    text = "The system message helps set the behavior of the assistant." +
                        " For example, you can modify the personality of the assistant " +
                        "or provide specific instructions about how it should behave throughout the conversation."
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Examples: \"You are a helpful assistant\", \"Parlami in dialetto romano\".\n" +
                            "Suggestion: You can use something like \"Be brief\" to speed up the response time.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Current system message: \"$currentSysMessage\"")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This action will clear the current chat",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row (verticalAlignment = Alignment.CenterVertically) {

                    Box (
                        modifier = Modifier.padding(top = 8.dp, end = 12.dp)
                    ){
                        Icon(Icons.TwoTone.Edit, contentDescription = null)
                    }

                    OutlinedTextField(
                        value = newBehaviour,
                        onValueChange = { value -> newBehaviour = value },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        label = { Text(text = "Custom message") },
                    )
                }
            }


        },
        confirmButton = {
            TextButton(onClick = { onConfirm(newBehaviour) }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )

}

@Preview
@Composable
fun showBehaviourDialogPreview(){
    DroidGPTTheme {
        ShowBehaviourDialog(
            onDismiss = { /*TODO*/ },
            onConfirm = {},
            currentSysMessage = "test"
        )
    }
}