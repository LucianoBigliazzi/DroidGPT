package com.droidgpt.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.droidgpt.R
import com.droidgpt.data.ConversationDatabase
import com.droidgpt.data.Data
import com.droidgpt.ui.common.performHapticFeedbackIfEnabled
import com.droidgpt.viewmodel.ChatViewModel
import com.droidgpt.ui.theme.DroidGPTTheme
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val titleInput = "Generate a title for this conversation. It must be short, maximum 4 words. No punctuation, translate in the language of the first message sent."

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserInput(viewModel: ChatViewModel, listState: () -> LazyListState){

    var msg by remember {
        mutableStateOf("")
    }


    val haptic = LocalHapticFeedback.current
    LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    rememberCoroutineScope()

    val callback = {
        focusManager.clearFocus()
    }

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val windowInfo = LocalWindowInfo.current

    var closeKeyboard by remember {
        mutableStateOf(false)
    }

    var generateTitle by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    Row (
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(16.dp, 8.dp, 0.dp, 8.dp)
            .wrapContentHeight()
            .heightIn(0.dp, 200.dp)
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .weight(1f)
        ) {

            TextField(
                value = msg,
                onValueChange = {text -> msg = text},
                placeholder = { Text(text = "Enter message", color = MaterialTheme.colorScheme.onSurfaceVariant) },      //Replaces label
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                keyboardActions = KeyboardActions(
                    onDone = { closeKeyboard = true }
                )
            )

            // Button to clear the current input in the TextField
            IconButton(
                onClick = {
                    msg = ""
                    performHapticFeedbackIfEnabled(haptic, viewModel.isHapticEnabled.value, HapticFeedbackType.LongPress)
                },
                modifier = Modifier.padding(end = 8.dp),
                enabled = msg.length > 1
            ) {
                AnimatedVisibility(
                    visible = msg.length > 1,
                    enter = slideInHorizontally() + fadeIn(tween(175)),
                    exit = slideOutHorizontally() + fadeOut(tween(175))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.backspace),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

        }

        Spacer(modifier = Modifier.width(8.dp))

        Box (
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            IconButton(
                enabled = msg.isNotBlank() && !viewModel.isLoading(),
                onClick = {
                    performHapticFeedbackIfEnabled(haptic, viewModel.isHapticEnabled.value, HapticFeedbackType.LongPress)
                    msg = msg.trim()
                    callback()

                    scope.launch {
                        println("MESSAGE: $msg")
                        if(viewModel.stream.value)
                            viewModel.chunkCompletion(msg, haptic)
                        else
                            viewModel.apiCallUsingLibrary(msg)
                    }
                    //msg = ""    // Since I included a backspace button, I may remove this line
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

    }

    LaunchedEffect(closeKeyboard){
        if(closeKeyboard){
            callback()
            closeKeyboard = false
        }
    }

    LaunchedEffect(viewModel.libraryMsgList.size){
        if(viewModel.libraryMsgList.size % 2 == 0 && viewModel.libraryMsgList.size > 1)
            listState().animateScrollToItem(viewModel.libraryMsgList.size - 1)
        else
            listState().animateScrollToItem(viewModel.libraryMsgList.size)
    }



    if(viewModel.getGenerateCompletion())
        LaunchedEffect(viewModel.getGenerateCompletion()){
            viewModel.setGenerateCompletion(false)
        }

    LaunchedEffect(windowInfo){
//        snapshotFlow { windowInfo.isWindowFocused }.collect {isWindowFocused ->
//            if(isWindowFocused) {
//                delay(100)
//                focusRequester.requestFocus()
//            }
//        }

        awaitFrame()
        delay(100)
        focusRequester.requestFocus()
    }


}


@Preview(showBackground = true)
@Composable
fun UserInputPreview(){

    val context = LocalContext.current
    val state = rememberLazyListState()
    val data = Data(context)
    val db = Room.databaseBuilder(context, ConversationDatabase::class.java, "conversation.db").build()
    val viewModel = ChatViewModel(data, db.dao)

    DroidGPTTheme {
        UserInput(viewModel = viewModel) { state }
    }
}