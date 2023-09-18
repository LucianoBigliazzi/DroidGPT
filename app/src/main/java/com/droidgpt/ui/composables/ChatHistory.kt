package com.droidgpt.ui.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.droidgpt.R
import com.droidgpt.data.ConversationEvent
import com.droidgpt.data.ConversationState
import com.droidgpt.data.ConversationUpdate
import com.droidgpt.model.MessageData
import com.droidgpt.model.TimeFormats
import com.droidgpt.ui.chat.BubbleIn
import com.droidgpt.ui.chat.BubbleOut
import com.droidgpt.ui.common.ChangePropertyDialog
import com.droidgpt.ui.common.ChatHistoryLazyListItem
import com.droidgpt.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistory(navController: NavHostController, viewModel: ChatViewModel, conversationState: ConversationState){

    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    
    Scaffold (
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TopAppBar(
            title = { Text(text = "History", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() },
                    content = { Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(id = R.string.back)) }
                )
            }
        )},
        content = {paddingValues ->
            ChatHistoryContent(paddingValues, viewModel, navController, conversationState)
        }
    )
}

@Composable
fun ChatHistoryContent(
    paddingValues: PaddingValues,
    viewModel: ChatViewModel,
    navController: NavHostController,
    conversationState: ConversationState
) {


//    Box (
//        modifier = Modifier.padding(paddingValues = paddingValues).fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "Not yet implemented", style = MaterialTheme.typography.labelLarge, fontStyle = FontStyle.Italic)
//    }

    val scope = rememberCoroutineScope()

    var itemVisible by remember {
        mutableStateOf(true)
    }

    var showEditTitleDialog by remember {
        mutableStateOf(false)
    }

    var editedTitle by remember {
        mutableStateOf("")
    }

    var currentIndex by remember {
        mutableIntStateOf(-1)
    }
    var currentID by remember {
        mutableIntStateOf(-1)
    }
    var showConversationPreview by remember {
        mutableStateOf(false)
    }

    val currentList = remember {
        mutableStateListOf<MessageData>()
    }
    var currentTitle by remember {
        mutableStateOf("")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                top = paddingValues.calculateTopPadding(),
                end = 20.dp,
                bottom = paddingValues.calculateBottomPadding()
            )
    ){

        itemsIndexed(conversationState.conversations){index, conversation ->
//            DateTitle(
//                conversations = conversationState.conversations,
//                currentConversation = conversation,
//                date = conversation.messagesList[1].messageTime.format(TimeFormats.DATE)
//            )

            Column {
                if(index != 0) {
                    if (conversationState.conversations[index - 1].creationDate != conversation.creationDate)
                        DateTitle(date = conversation.creationDate.format(TimeFormats.DATE_TXT))
                } else
                    DateTitle(date = conversation.creationDate.format(TimeFormats.DATE_TXT))



                ChatHistoryLazyListItem(
                    date = conversation.messagesList[1].messageTime,
                    title = conversation.title,
                    onEditTitle = {
                        currentIndex = index
                        currentID = conversation.id
                        showEditTitleDialog = true
                    },
                    onDelete = {
                        itemVisible = false
                        viewModel.onEvent(ConversationEvent.DeleteConversation(conversation))
                        if(conversation.messagesList == viewModel.libraryMsgList.toList())
                            viewModel.clearList()
                    },
                    onLongClick = {
                        showConversationPreview = true
                        currentIndex = index
                        currentList.clear()
                        conversation.messagesList.forEach {
                            currentList.add(it)
                        }
                        currentTitle = conversation.title
                    },
                    onClick = {
                        viewModel.libraryMsgList.clear()
                        conversation.messagesList.forEach {
                            viewModel.addToLibraryList(it)
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    val context = LocalContext.current

    if(showConversationPreview){
        ShowConversationPreviewDialog(
            onDismiss = { showConversationPreview = false },
            onConfirm = {
                viewModel.libraryMsgList.clear()
                currentList.forEach {
                    viewModel.addToLibraryList(it)
                }
                navController.popBackStack()
            },
            editTitle = {  },
            conversationTitle = currentTitle,
            list = currentList
        )
    }


    if(showEditTitleDialog){
        ChangePropertyDialog(
            title = "Change title",
            onConfirm = {
                editedTitle = editedTitle.trim()
                if(editedTitle.isNotEmpty()) {
                    scope.launch {
                        viewModel.conversationDao.updateTitle(
                            ConversationUpdate(
                                currentID,
                                editedTitle
                            )
                        )
                    }
                    showEditTitleDialog = false
                    Toast.makeText(context, "Title updated", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(context, "Enter a custom title", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showEditTitleDialog = false }) {
            Column {

                Text(
                    text = "Modify the title of this conversation.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row (verticalAlignment = Alignment.CenterVertically) {

                    Box (
                        modifier = Modifier.padding(top = 8.dp, end = 12.dp)
                    ){
                        Icon(Icons.Outlined.Edit, contentDescription = null)
                    }

                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { value -> editedTitle = value },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        label = { Text(text = "Enter new title") },
                    )
                }
            }
        }
    }
}

@Composable
fun ShowConversationPreviewDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    editTitle: () -> Unit,
    conversationTitle: String,
    list: List<MessageData>
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { ConfirmButton(onClick = onConfirm) },
        dismissButton = { DismissButton(onClick = onDismiss)},
        title = { AlertDialogTitle(conversationTitle, list.size - 1, onClick = editTitle) },
        text = { PreviewContent(list = list) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    )
}

@Composable
fun AlertDialogTitle(
    conversationTitle: String,
    size: Int,
    onClick: () -> Unit
) {

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = conversationTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = msgNum(size), style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onClick) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
        }
    }
}

fun msgNum(size: Int): String {

    if(size == 1)
        return "$size message"

    return "$size messages"
}

@Composable
fun PreviewContent(list: List<MessageData>) {
    Box (
        modifier = Modifier.clip(RoundedCornerShape(20.dp))
    ) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp)
        ){

            items(list){
                if(it.chatMessage.role == ChatRole.User)
                    BubbleOut(messageData = it, isHapticEnabled = false)
                else if(it.chatMessage.role == ChatRole.Assistant)
                    BubbleIn(messageData = it, isHapticEnabled = false)
            }
        }
    }
}

@Composable
fun ConfirmButton(
    onClick: () -> Unit
){
    Button(
        onClick = onClick
    ) {
        //Text(text = "Check out")
        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Check out conversation")
    }
}

@Composable
fun DismissButton(
    onClick: () -> Unit
){
    TextButton(
        onClick = onClick
    ) {
        Text(text = "Cancel")
    }
}

@Composable
fun DateTitle(
    date: String
){
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = date,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun takeTitle(string: String) : String {

    val finalString = StringBuilder()

    if(string.length > 24){
        finalString.append(string.take(24).trim())
        finalString.append("...")
        return finalString.toString()
    }

    return string
}

@Preview
@Composable
fun PreviewDialogPreview(){

    MaterialTheme {
        Surface {
            ShowConversationPreviewDialog(
                onDismiss = {  },
                onConfirm = {  },
                editTitle = {  },
                conversationTitle = "Title",
                list = listOf(
                    MessageData(ChatMessage(ChatRole.User, "Ciao come va"), LocalDateTime.now()),
                    MessageData(ChatMessage(ChatRole.Assistant, "Ciao!"), LocalDateTime.now())
                )
            )
        }
    }
}