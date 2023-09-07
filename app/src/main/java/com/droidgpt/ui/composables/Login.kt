package com.droidgpt.ui.composables

import android.content.Context
import android.content.SharedPreferences
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidgpt.data.Data
import com.droidgpt.data.KeyManager
import com.droidgpt.data.labels.SettingsLabels
import com.droidgpt.viewmodel.ChatViewModel
import com.droidgpt.ui.common.Route
import com.droidgpt.ui.common.Tips
import com.droidgpt.ui.theme.DroidGPTTheme
import com.droidgpt.ui.theme.parseSurfaceColor


fun checkProvidedKey(key : String, context : Context): Boolean {

    if(key.isBlank())
        return false

    val sharedPreferences : SharedPreferences = context.getSharedPreferences(SettingsLabels.SETTINGS, Context.MODE_PRIVATE)


    if(KeyManager.validateKey(key)){
        sharedPreferences.edit().putString(SettingsLabels.API_KEY, key).apply()
        return true
    }
    return false
}


@Composable
fun Login(navController: NavHostController, viewModel: ChatViewModel, window: Window){

    WindowCompat.setDecorFitsSystemWindows(window, false)
    LoginScreen(navController = navController, viewModel = viewModel)
}


@Composable
fun LoginScreen(navController: NavHostController, viewModel: ChatViewModel) {

    val context = LocalContext.current

    var goToMain by remember {
        mutableStateOf(false)
    }

    var showError by remember { mutableStateOf(false) }

    DroidGPTTheme (
        darkTheme = if(viewModel.isSystemTheme()) isSystemInDarkTheme() else viewModel.isDarkTheme(),
        isHighContrastModeEnabled = viewModel.highContrast.value
    ) {
        Scaffold (
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    if(checkProvidedKey(viewModel.getKey(), context = context))
                        goToMain = true
                    else
                        showError = true
                }) {
                    Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = null)
                }
            },
            modifier = Modifier.imePadding(),
            containerColor = parseSurfaceColor(viewModel = viewModel)
        ) { paddingValues ->
            LoginContent(paddingValues, viewModel, showError)
        }
    }

    LaunchedEffect(goToMain){
        if(goToMain){
            goToMain = false
            navController.navigate(Route.MAIN)
        }
    }

}

@Composable
fun LoginContent(paddingValues: PaddingValues, viewModel: ChatViewModel, showError: Boolean) {

    val context : Context = LocalContext.current

    var key by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(128.dp))

        Box (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(128.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = key,
                onValueChange = {
                    viewModel.setKey(it)
                    key = viewModel.getKey()
                },
                label = { Text(text = "Enter your API key") },
                modifier = Modifier.weight(1f),
                isError = showError,
                supportingText = {
                    if(showError){
                        Text(text = "Invalid API key",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )
        }

        val uriHandler = LocalUriHandler.current

        Tips(text = annotatedString()) {
            uriHandler.openUri("https://beta.openai.com/account/api-keys")
        }

    }
}


@Composable
fun annotatedString() : AnnotatedString {

    val annotatedText = buildAnnotatedString {
        //append your initial text
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light
            )
        ) {
            append("Make sure to set up an OpenAI account, you can generate an api key ")

        }

        //Start of the pushing annotation which you want to color and make them clickable later
        pushStringAnnotation(
            tag = "link",// provide tag which will then be provided when you click the text
            annotation = "link"
        )
        //add text with your different color/style
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Light
            )
        ) {
            append("here")
        }
        // when pop is called it means the end of annotation with current tag
        pop()
    }

    return annotatedText
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
    val viewModel = ChatViewModel(Data(LocalContext.current))

    DroidGPTTheme {
        LoginScreen(navController = navController, viewModel = viewModel)
    }
}