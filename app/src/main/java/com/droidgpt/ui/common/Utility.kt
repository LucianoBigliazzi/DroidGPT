package com.droidgpt.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.droidgpt.R

@Composable
fun DropdownInfo(
    title: String,
    content: @Composable () -> Unit
) {

    var check by remember {
        mutableStateOf(false)
    }

    Column {
        TextButton(onClick = {check = !check}) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if(check) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.info)
                )

                Text(text = title)
            }
        }

        if(check)
            content.invoke()
    }
}