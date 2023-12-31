package com.droidgpt.ui.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aallam.openai.api.BetaOpenAI
import com.droidgpt.R
import com.droidgpt.data.database.ConversationEvent
import com.droidgpt.viewmodel.ChatViewModel
import com.droidgpt.ui.common.ClearChatDialog
import com.droidgpt.ui.common.Route
import com.droidgpt.ui.common.TopBarTitle
import com.droidgpt.ui.common.performHapticFeedbackIfEnabled
import com.droidgpt.ui.theme.DroidGPTTheme
import com.droidgpt.ui.theme.parseSurfaceColor
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, BetaOpenAI::class)
@Composable
fun ActionBar(
    viewModel: ChatViewModel,
    navController: NavHostController
) {

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var animatedTitle by remember {
        mutableStateOf(AnnotatedString(""))
    }

    //val title = "DroidGPT!".toCharArray()
    MaterialTheme.typography.titleMedium
    rememberCoroutineScope()

    var clearChat by remember{
        mutableStateOf(false)
    }

    var connectionMark by remember {
        mutableStateOf(false)
    }

    var goToSettings by remember {
        mutableStateOf(false)
    }
    var goToChatHistory by remember {
        mutableStateOf(false)
    }

    val title = stringResource(id = R.string.app_name)


    /**
     * Animation thread for action bar title
     *
     */
//    LaunchedEffect(title){
//
//        scope.launch {
//            delay(500)
//
//            buildAnnotatedString {
//                title.forEachIndexed { index, char ->
//                    append(char)
//                    animatedTitle = this.toAnnotatedString()
//                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
//                    delay(50)
//                }
//            }
//        }
//    }

    println("TOP APP BAR RECOMPOSED")


    TopAppBar(
        title = {

            Row {
                if(connectionMark)
                    TopBarTitle(text = title, false, modifier = Modifier.align(Alignment.CenterVertically))
                else
                    TopBarTitle(text = title, isError = true, modifier = Modifier.align(Alignment.CenterVertically))

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { goToChatHistory = true }, modifier = Modifier.align(Alignment.CenterVertically)){
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.history),
                        contentDescription = stringResource(id = R.string.toggle_drawer)
                    )
                }
            }


        },

        actions = {


            ClearButton(
                context = context,
                clearChat = { clearChat = true },
                msgListSize = viewModel.libraryMsgList.size,
                isLoading = viewModel.isLoading(),
                haptic = haptic,
                isHapticEnabled = viewModel.isHapticEnabled.value
            )

            IconButton(onClick = {
                goToSettings = true
            }) {
                Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            //scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = parseSurfaceColor(highContrast = viewModel.highContrast.value)
        ),
        //scrollBehavior = scrollBehavior
    )

    LaunchedEffect(goToSettings){
        if(goToSettings) {
            delay(50)
            navController.navigate(Route.SETTINGS)
        }
    }

    LaunchedEffect(goToChatHistory){
        if(goToChatHistory) {
            delay(50)
            navController.navigate(Route.HISTORY)
        }
    }


    if(clearChat){
        ClearChatDialog(
            onDismiss = { clearChat = false },
            onConfirm = {
                clearChat = false

                //viewModel.onEvent(ConversationEvent.SetTitle("title"))
                viewModel.onEvent(ConversationEvent.SetMessageDataList(viewModel.libraryMsgList.toList()))
                viewModel.onEvent(ConversationEvent.SaveConversation)
                Toast.makeText(context, "Cleared ${viewModel.libraryMsgList.size - 1} messages", Toast.LENGTH_SHORT).show()
                viewModel.clearList()
            }
        )
    }

    LaunchedEffect(viewModel.connectionEstablished){
        connectionMark = viewModel.connectionEstablished
    }
}


@Composable
fun ClearButton(
    context: Context,
    clearChat: () -> Unit,
    msgListSize: Int,
    isLoading: Boolean,
    haptic: HapticFeedback,
    isHapticEnabled: Boolean,
){

    IconButton(
        onClick = {

            if(msgListSize > 0){
                if(!isLoading){
                    clearChat()
                    performHapticFeedbackIfEnabled(haptic, isHapticEnabled, HapticFeedbackType.LongPress)
                }else{
                    Toast.makeText(context, "Wait for the reply", Toast.LENGTH_SHORT).show()
                    performHapticFeedbackIfEnabled(haptic, isHapticEnabled, HapticFeedbackType.LongPress)
                }
            }

        },
       enabled = !isLoading && msgListSize > 1
    ) {
        AnimatedVisibility(
            visible = msgListSize > 1,
            enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(tween(175)),
            exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut(tween(175))
        ) {
            Icon(Icons.Outlined.Add, stringResource(R.string.new_chat))
        }
    }
}

@Preview
@Composable
fun TopBarPreview(){

    val viewModel : ChatViewModel = viewModel()
    val navController = rememberNavController()

    DroidGPTTheme {
        ActionBar(viewModel = viewModel, navController = navController)
    }
}