package com.droidgpt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.droidgpt.viewmodel.ChatViewModel

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)



@Composable
fun parseSurfaceColor(highContrast: Boolean) : Color{

    return if(isSystemInDarkTheme() && highContrast) Color.Black
    else if(isSystemInDarkTheme() && !highContrast) MaterialTheme.colorScheme.surface
    else MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
}