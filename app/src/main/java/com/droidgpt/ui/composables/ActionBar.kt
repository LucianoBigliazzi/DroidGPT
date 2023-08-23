package com.droidgpt.ui.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidgpt.R
import com.droidgpt.data.Data
import com.droidgpt.model.ChatViewModel
import com.droidgpt.ui.common.ClearChatDialog
import com.droidgpt.ui.common.Route
import com.droidgpt.ui.common.TopBarTitle
import com.droidgpt.ui.theme.DroidGPTTheme
import com.droidgpt.ui.theme.parseSurfaceColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBar(
    viewModel: ChatViewModel,
    navController: NavHostController,
    data: Data
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
//        navigationIcon = { IconButton(onClick = { goToChatHistory = true }){
//            Icon(
//                modifier = Modifier.size(24.dp),
//                painter = painterResource(id = R.drawable.history),
//                contentDescription = stringResource(id = R.string.toggle_drawer)
//            ) }
//        },
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

            if(viewModel.msgList.size > 0){
                IconButton(onClick = {

                    if(viewModel.msgList.isNotEmpty()){
                        if(viewModel.msgList[viewModel.getMsgCount() - 1].reply != null){
                            clearChat = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }else{
                            Toast.makeText(context, "Wait for the reply", Toast.LENGTH_SHORT).show()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }else{
                        Toast.makeText(context, "No messages to clear", Toast.LENGTH_SHORT).show()
                    }

                }) {
                    Icon(Icons.Outlined.Delete, stringResource(R.string.new_chat))
                }
            }

            IconButton(onClick = {
                goToSettings = true
            }) {
                Icon(Icons.Outlined.Settings, stringResource(R.string.settings))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            //scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = parseSurfaceColor(viewModel = viewModel)
        ),
        //scrollBehavior = scrollBehavior
    )

    LaunchedEffect(goToSettings){
        if(goToSettings)
            navController.navigate(Route.SETTINGS)
    }

    LaunchedEffect(goToChatHistory){
        if(goToChatHistory)
            navController.navigate(Route.HISTORY)
    }


    if(clearChat){
        ClearChatDialog(
            onDismiss = { clearChat = false },
            onConfirm = {
                clearChat = false

                Toast.makeText(context, "Cleared ${viewModel.getMsgCount()} messages", Toast.LENGTH_SHORT).show()
                clearChat(viewModel, context, data)
            }
        )
    }

    LaunchedEffect(viewModel.connectionEstablished){
        connectionMark = viewModel.connectionEstablished
    }
}

fun clearChat(viewModel: ChatViewModel, context: Context, data: Data) {

    if(viewModel.msgList.isNotEmpty()){
        viewModel.clearList()
        //viewModel.addElementToHistory(data.getJsonString(context), context)
        data.deleteJson(context, data.conversationFileName)
        try {
            data.createJson(context, data.conversationFileName)
        } catch (e : Exception) {
            e.stackTrace
        }
    }
}

@Preview
@Composable
fun TopBarPreview(){

    val viewModel : ChatViewModel = viewModel()
    val navController = rememberNavController()
    val data = Data(LocalContext.current)

    DroidGPTTheme {
        ActionBar(viewModel = viewModel, navController = navController, data = data)
    }
}