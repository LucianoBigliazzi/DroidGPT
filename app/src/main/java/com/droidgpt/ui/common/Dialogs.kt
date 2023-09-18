package com.droidgpt.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidgpt.R
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
    onConfirm: (Float) -> Unit
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
            onDismiss = { /*TODO*/ }
        ) { }
    }
}


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
        title = { Text(text = "Change API key") },
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
        title = { Text(text = "System message") },
        text = {

            Column {

                DropdownInfo (
                    title = "Info",
                ){
                    Text(
                        text = stringResource(R.string.system_messag_info),
                        modifier = Modifier.padding(start = 37.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val currentSystemMessageStyled = buildAnnotatedString {
                    append("Current system message:\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)){
                        append(currentSysMessage)
                    }
                }

                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = currentSystemMessageStyled)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This action will clear the current chat",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                val default = stringResource(R.string.you_are_a_helpful_assistant)
                val suggestion = stringResource(R.string.be_brief)


                Row (
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(onClick = { newBehaviour = default }) {
                        Text(text = "Default")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(onClick = { newBehaviour = suggestion }) {
                        Text(text = suggestion)
                    }
                }



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
fun ShowBehaviourDialogPreview(){
    DroidGPTTheme {
        ShowBehaviourDialog(
            onDismiss = { /*TODO*/ },
            onConfirm = {},
            currentSysMessage = "test"
        )
    }
}


@Composable
fun ChangePropertyDialog(
    title: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
){

    val newTitle by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = content,
        confirmButton = {
            TextButton(onClick = { onConfirm(newTitle) }) {
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
