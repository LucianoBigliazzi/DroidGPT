package com.droidgpt.ui.chat

import android.content.Context
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.droidgpt.R
import com.droidgpt.data.Data
import com.droidgpt.model.ApiReply
import com.droidgpt.model.ChatMessage
import com.droidgpt.viewmodel.ChatViewModel
import com.droidgpt.ui.theme.DroidGPTTheme
import kotlinx.coroutines.launch

const val titleInput = "Generate a title for this conversation. It must be short, maximum 4 words. No punctuation, translate in the language of the first message sent."

@OptIn(ExperimentalComposeUiApi::class, BetaOpenAI::class)
@Composable
fun UserInput(viewModel: ChatViewModel, listState: () -> LazyListState, data: Data){

    val context = LocalContext.current

    var msg by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    rememberCoroutineScope()

    val callback = {
        focusManager.clearFocus()
    }

    var closeKeyboard by remember {
        mutableStateOf(false)
    }

    var generateTitle by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    var timeOut : Long
    var timeIn : Long

    Row (
        verticalAlignment = Alignment.CenterVertically,
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
                modifier = Modifier.weight(1f),
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

            if(msg.length > 1){

                // Button to clear the current input in the TextField
                IconButton(
                    onClick = {
                        msg = ""
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier.padding(end = 8.dp)
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

        var completion : ChatCompletion

        Box (
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            IconButton(
                enabled = msg.isNotBlank() && !viewModel.isLoading(),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    msg = msg.trim()
                    callback()
//                        viewModel.performApiCall(
//                            question = msg,
//                            data = data,
//                            context = context,
//                            haptic = haptic,
//                            view = view
//                        )


                    scope.launch {
                        println("MESSAGE: $msg")
                        if(viewModel.stream.value)
                            viewModel.chunkCompletion(msg)
                        else
                            viewModel.apiCallUsingLibrary(msg)
                    }
                    //msg = ""    // Since I included a backspace button, I may remove this line
                }

            ) {
                if(msg.isNotBlank() && !viewModel.isLoading())
                    Icon(Icons.Outlined.Send, null, tint = MaterialTheme.colorScheme.primary)
                else
                    Icon(Icons.Outlined.Send, null)
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



    var check by remember {
        mutableStateOf(false)
    }
    var text by remember {
        mutableStateOf("")
    }

//    LaunchedEffect(generateTitle){
//
//
//        if(generateTitle){
//            withContext(Dispatchers.IO){
//                data.titleGeneration(titleInput) {reply ->
//                    if(reply != null && !reply.error){
//                        check = true
//                        text = reply.text
//                    }
//                }
//
//                //data.removeTitleQuestionFromJson(context, viewModel.getMsgCount())
//            }
//
//            generateTitle = false
//        }
//
//
//        // Animation for title: print char one by one like chatgpt style (stream not developed yet)
//        if(check){
//            viewModel.setChatTitle("")
//            for(c in text){
//                delay(50)
//                viewModel.setChatTitle(viewModel.getChatTitle().plus(c))
//            }
//            check = false
//        }
//    }




}


@Preview(showBackground = true)
@Composable
fun UserInputPreview(){

    val context = LocalContext.current
    val state = rememberLazyListState()
    val data = Data(context)
    val viewModel = ChatViewModel(data)

    DroidGPTTheme {
        UserInput(viewModel = viewModel, { state }, data = data)
    }
}