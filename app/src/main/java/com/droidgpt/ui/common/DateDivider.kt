package com.droidgpt.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidgpt.data.TimeFormats
import com.droidgpt.ui.theme.DroidGPTTheme
import java.time.LocalDateTime

@Composable
fun DateDivider(localDateTime: LocalDateTime){
    
    Row (
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box (
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = localDateTime.format(TimeFormats.DATE_TXT),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Preview
@Composable
fun DateDividerPreview(){
    
    DroidGPTTheme {
        DateDivider(localDateTime = LocalDateTime.now())
    }
}