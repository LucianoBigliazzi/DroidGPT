package com.droidgpt

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.droidgpt.data.Data
import com.droidgpt.data.labels.SettingsLabels
import com.droidgpt.model.ChatViewModel
import com.droidgpt.ui.common.Route
import com.droidgpt.ui.common.animatedComposable
import com.droidgpt.ui.composables.ChatHistory
import com.droidgpt.ui.composables.Login
import com.droidgpt.ui.composables.MainScreen
import com.droidgpt.ui.composables.SettingsScreen
import com.droidgpt.ui.theme.DroidGPTTheme

class App : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DroidGPTTheme {
                AppNavigation(window)
            }
        }
    }
}

@Composable
fun AppNavigation(window: Window) {

    val context = LocalContext.current
    val data = Data(context)
    val viewModel : ChatViewModel = viewModel()
    val navController = rememberNavController()

    val startDestination : String
    val apiKey : String = data.resolveKeyShared()

    if(apiKey != "null"){
        startDestination = Route.MAIN
    } else {
        startDestination = Route.LOGIN
    }

    //data.initializeJson() initialized in Data constructor
    viewModel.darkTheme.value = data.getBooleanFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, isSystemInDarkTheme())
    viewModel.highContrast.value = data.getBooleanFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, false)
    viewModel.setSystemTheme(data.getBooleanFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.SYSTEM_THEME, isSystemInDarkTheme()))
    //viewModel.retrieveList(data.getFromSharedPreferences(DataLabels.DATA, DataLabels.HISTORY))

    NavHost(navController = navController, startDestination = startDestination) {
        animatedComposable(Route.MAIN) {
            //init(context)
            MainScreen(navController = navController, window = window, data = data, viewModel = viewModel)
        }
        animatedComposable(Route.SETTINGS){
            SettingsScreen(navController = navController, window = window, data = data, viewModel = viewModel)
        }
        animatedComposable(Route.LOGIN) {
            Login(navController = navController, viewModel)
        }
        animatedComposable(Route.HISTORY){
            ChatHistory(navController = navController, viewModel = viewModel, data = data)
        }
    }
}



