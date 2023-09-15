package com.droidgpt.ui.common

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun performHapticFeedbackIfEnabled(haptic: HapticFeedback, enabled: Boolean, hapticFeedbackType: HapticFeedbackType){

    if(enabled)
        haptic.performHapticFeedback(hapticFeedbackType)
}