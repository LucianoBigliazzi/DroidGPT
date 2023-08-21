package com.droidgpt.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidgpt.ui.theme.DroidGPTTheme


//@Composable
//fun TemperatureSlider(temperatureValue: Float) {
//
//
//    val haptic = LocalHapticFeedback.current
//
//    Column (
//        modifier = Modifier.padding(top = 24.dp)
//    ) {
//        Slider(
//            value = selectedTemperature.toFloat(),
//            onValueChange = { value -> temperatureValue = value.toDouble()},
//            steps = 10
//        )
//
//        Text(
//            text = selectedTemperature.toString(),
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            style = MaterialTheme.typography.labelLarge
//        )
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun TemperatureSliderPreview(){
//    DroidGPTTheme {
//        TemperatureSlider(temperatureValue)
//    }
//}