package com.droidgpt.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
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


@Composable
fun Tips(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = stringResource(R.string.tips),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = text,
            onClick = { offset ->
                text.getStringAnnotations(
                    tag = "link",// tag which you used in the buildAnnotatedString
                    start = offset,
                    end = offset
                )[0].let {
                    onClick.invoke()
                }
            }
        )
    }
}