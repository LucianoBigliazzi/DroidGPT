package com.droidgpt.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.droidgpt.R
import com.droidgpt.model.ChatMessage
import com.droidgpt.ui.common.DotsTyping
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class, BetaOpenAI::class)
@Composable
fun BubbleOut(
    chatMessage: com.aallam.openai.api.chat.ChatMessage
){

    val haptic = LocalHapticFeedback.current
    val clipboardManager = ContextCompat.getSystemService(LocalContext.current, ClipboardManager::class.java)

    val time = SimpleDateFormat("HH:mm", Locale.ROOT).format(System.currentTimeMillis())
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        ){
            append(stringResource(R.string.you))
            append(" ")
        }
        withStyle(style = SpanStyle(
            fontSize = 11.sp
        )
        ){
            append(time)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp, 0.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.End,
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = annotatedString,
            modifier = Modifier
                .padding(end = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )

        Spacer(modifier = Modifier.height(2.dp))

        chatMessage.content?.let {
            DisplayContentText(
                text = it,
                error = false,
                isSent = true,
                clipboardManager = clipboardManager,
                haptic = haptic
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
fun ReplyBubble(chatMessage: ChatMessage){

//    if(chatMessage.reply != null)
//        BubbleIn(chatMessage = chatMessage)
//    else
//        BubbleLoading()
}


@OptIn(BetaOpenAI::class)
@Composable
fun BubbleIn(
    chatMessage: com.aallam.openai.api.chat.ChatMessage
){

    val haptic = LocalHapticFeedback.current
    val clipboardManager = ContextCompat.getSystemService(LocalContext.current, ClipboardManager::class.java)

    val time = SimpleDateFormat("HH:mm", Locale.ROOT).format(System.currentTimeMillis())

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        ){
            append(stringResource(R.string.app_name))
            append(" ")
        }

        withStyle(style = SpanStyle(
            fontSize = 11.sp
        )
        ){
            append(time)
        }
    }

    //haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    println("HAPTIC")

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 64.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = annotatedString,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        //resolveText(input = msg.text)

        chatMessage.content?.let {
            DisplayContentText(
                text = it,
                error = false,
                isSent = false,
                clipboardManager = clipboardManager,
                haptic = haptic
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

//        Text(
//            text = resolveText(text),
//            textAlign = TextAlign.Start,
//            fontSize = 16.sp,
//            modifier = Modifier
//                .background(
//                    color = MaterialTheme.colorScheme.primaryContainer,
//                    shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
//                )
//                .padding(16.dp, 8.dp, 16.dp, 8.dp),
//            color = MaterialTheme.colorScheme.onPrimaryContainer,
//        )
    }
}


@Composable
fun BubbleLoading(){

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 64.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(2.dp))

        //resolveText(input = msg.text)

        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
                )
                .padding(16.dp, 8.dp, 16.dp, 8.dp),
        ) {
            DotsTyping()
        }

        Spacer(modifier = Modifier.height(2.dp))
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayContentText(
    text: String,
    error: Boolean,
    isSent: Boolean,
    clipboardManager: ClipboardManager?,
    haptic: HapticFeedback,
){

    Text(
        text = text,
        color = if(!error) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer,
        modifier = Modifier
            .background(
                color = parseColor(
                    isError =       error,
                    isSent =        isSent,
                    outgoingColor = MaterialTheme.colorScheme.primaryContainer,
                    incomingColor = MaterialTheme.colorScheme.secondaryContainer,
                    errorColor =    MaterialTheme.colorScheme.errorContainer),
                shape = if(isSent) RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp) else RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
            )
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .combinedClickable(
                enabled = true,
                onLongClickLabel = "bubble long click",
                onLongClick = {
                    if (clipboardManager != null) {
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("message text", text))
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                },
                onClick = {},
                onDoubleClick = {}
            ),
    )
}


fun parseColor(
    isError: Boolean,
    isSent: Boolean,
    outgoingColor: Color,
    incomingColor: Color,
    errorColor: Color
) : Color{

    if(isError)
        return errorColor

    return if(isSent)
        outgoingColor
    else
        incomingColor
}

@OptIn(BetaOpenAI::class)
@Preview(showBackground = true)
@Composable
fun ChatPreviewIn(){
    MaterialTheme {
        Column {
            BubbleIn(com.aallam.openai.api.chat.ChatMessage(ChatRole.Assistant, "Ciao come va?"))

            BubbleIn(com.aallam.openai.api.chat.ChatMessage(ChatRole.Assistant, "Ciao come va?"))
        }
    }
}

@OptIn(BetaOpenAI::class)
@Preview(showBackground = true)
@Composable
fun ChatPreviewOut(){
    MaterialTheme {
        BubbleOut(com.aallam.openai.api.chat.ChatMessage(ChatRole.User, "Ciao come va"))
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleLoadingPreview(){
    MaterialTheme {
        BubbleLoading()
    }
}