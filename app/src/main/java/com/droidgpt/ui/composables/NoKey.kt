package com.droidgpt.ui.composables

import android.content.Context
import android.content.SharedPreferences
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidgpt.data.KeyManager
import com.droidgpt.data.labels.SettingsLabels
import com.droidgpt.model.ChatViewModel
import com.droidgpt.ui.common.Route
import com.droidgpt.ui.theme.DroidGPTTheme


fun checkProvidedKey(key : String, context : Context): Boolean {

    val sharedPreferences : SharedPreferences = context.getSharedPreferences(SettingsLabels.SETTINGS, Context.MODE_PRIVATE)


    if(KeyManager.validateKey(key)){
        sharedPreferences.edit().putString(SettingsLabels.API_KEY, key).apply()
        return true
    }
    return false
}


@Composable
fun LoginScreen(navController: NavHostController, viewModel: ChatViewModel, window: Window) {

    WindowCompat.setDecorFitsSystemWindows(window, false)

    Login(navController, viewModel)

}

@Composable
fun Login(navController: NavHostController, viewModel: ChatViewModel) {

    val context : Context = LocalContext.current

    DroidGPTTheme (
        darkTheme = if(viewModel.isSystemTheme()) isSystemInDarkTheme() else viewModel.isDarkTheme(),
        isHighContrastModeEnabled = viewModel.highContrast.value
    ) {
        var key by remember {
            mutableStateOf("")
        }
        var showError by remember { mutableStateOf(false) }

        var goToMain by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(140.dp))

            Text(
                text = "Provide your API key",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 24.dp)
            ) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text(text = "Enter key") },
                    modifier = Modifier.weight(1f),
                    isError = showError,
                    supportingText = {
                        if(showError){
                            Text(text = "Invalid key",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                )

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(
                    onClick = {
                        if(key.isNotBlank()){
                            if(checkProvidedKey(key, context))
                                goToMain = true
                            else
                                showError = true;
                        }
                    }
                ) {
                    Text(text = "GO", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                }

                LaunchedEffect(goToMain){
                    if(goToMain){
                        goToMain = false
                        navController.navigate(Route.MAIN)
                    }
                }
            }


            Row (
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(Icons.Outlined.Info, null,
                    Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .align(Alignment.CenterVertically),
                    tint = Color.White
                )

                Box {
                    ClickableHyperlink()
                }
            }

        }
    }
}

@Composable
fun ClickableHyperlink() {

    val uri = LocalUriHandler.current

    val text = buildAnnotatedString {

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
            append("Make sure you set up an OpenAI account, you can generate an api key ")
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append("here")
            addStringAnnotation("URL", "https://beta.openai.com/account/api-keys", start = 0, end = 4)
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
            append(".")
        }
    }

    ClickableText(
        text = text,
        onClick = { offset ->
            text.getStringAnnotations("URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uri.openUri(annotation.item)
                }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(){

    val navController = rememberNavController()
    val viewModel = ChatViewModel()

    DroidGPTTheme {
        Login(navController = navController, viewModel = viewModel)
    }
}