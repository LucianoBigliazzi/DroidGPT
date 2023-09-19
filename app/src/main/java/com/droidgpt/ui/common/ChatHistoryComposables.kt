package com.droidgpt.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidgpt.R
import com.droidgpt.model.TimeFormats
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatHistoryLazyListItem(
    date : LocalDateTime,
    title : String?,
    onEditTitle: () -> Unit,
    onDelete: () -> Unit,
    onLongClick: () -> Unit,
    onClick : () -> Unit
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box (
            modifier = Modifier
                .weight(1f)
                .combinedClickable(
                    onLongClick = onLongClick,
                    onClick = {},
                    onDoubleClick = {}
                )
        ) {
            TextButton(
                onClick = onClick
            ) {
                Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(
                        text = date.format(TimeFormats.TIME),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start),
                        fontSize = 12.sp
                    )
                    Text(
                        text = title.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 2.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }

        Box {
            IconButton(
                onClick = onLongClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(painter = painterResource(R.drawable.preview), contentDescription = null)
            }
        }

//        Box {
//            IconButton(
//                onClick = onEditTitle
//            ) {
//                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit title")
//            }
//        }

        Box {
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "delete conversation"
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemPreview(){
    Surface {
        ChatHistoryLazyListItem(date = LocalDateTime.now(), title = "Title", onEditTitle = {}, onDelete = {}, onClick = {},
        onLongClick = {})
    }
}