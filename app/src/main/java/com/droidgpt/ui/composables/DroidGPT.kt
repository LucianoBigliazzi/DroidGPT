package com.droidgpt.ui.composables

import android.annotation.SuppressLint
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidgpt.data.Data
import com.droidgpt.data.KeyManager
import com.droidgpt.data.TextCode
import com.droidgpt.data.TextResolver
import com.droidgpt.model.ChatViewModel
import com.droidgpt.model.TextMessage
import com.droidgpt.ui.chat.BubbleOut
import com.droidgpt.ui.chat.ReplyBubble
import com.droidgpt.ui.chat.UserInput
import com.droidgpt.ui.common.SnackbarVisualsWithError
import com.droidgpt.ui.theme.DroidGPTTheme


@Composable
fun MainScreen(
    navController: NavHostController,
    window: Window,
    data: Data,
    viewModel: ChatViewModel
){

    val context = LocalContext.current


    DroidGPTTheme (
        darkTheme = if(viewModel.isSystemTheme()) isSystemInDarkTheme() else viewModel.isDarkTheme(),
        isHighContrastModeEnabled = viewModel.highContrast.value
    ) {

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ScaffoldTest(navController, data, viewModel)
    }

    LaunchedEffect(true){
        if(!viewModel.connectionEstablished){
            if(KeyManager.validateKey(data.resolveKeyShared())){
                //Toast.makeText(context, "Key OK", Toast.LENGTH_SHORT).show()
                viewModel.connectionEstablished = true
                println("ONLINE")
            }else{
                Toast.makeText(context, "No key!", Toast.LENGTH_SHORT).show()
                viewModel.connectionEstablished = false
                println("OFFLINE")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScaffoldTest(navController: NavHostController, data: Data, viewModel: ChatViewModel) {

    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val state = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold (
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { ActionBar(viewModel, scrollBehavior, navController, data = data, snackbarHost = snackbarHostState, onDrawerClick = {} ) },

//        floatingActionButton = {
//            if(state.canScrollForward)
//                SmallFab({ state }, viewModel)
//        },
        content = { paddingValues ->
            Conversation(paddingValues = paddingValues, viewModel, stateProvider = { state }, data = data)
        },

        containerColor = if(viewModel.highContrast.value) Color.Black else MaterialTheme.colorScheme.surface,

        snackbarHost = {
            SnackbarHost (snackbarHostState) { data ->

                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
                val buttonColor = if (isError) {
                    ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.inversePrimary
                    )
                }


                Snackbar(
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.secondary)
                        .padding(12.dp),
                    action = {
                        TextButton(
                            onClick = { if (isError) data.dismiss() else data.performAction() },
                            colors = buttonColor
                        ) { Text(data.visuals.actionLabel ?: "") }
                    }
                ) {
                    Text(data.visuals.message)
                }
            }
        },

        //bottomBar = { InputUser(viewModel = viewModel, stateProvider = { state }, data = data) }


    )
}


@Composable
fun SmallFab(stateProvider: () -> LazyListState, viewModel: ChatViewModel) {

    var clicked by remember {
        mutableStateOf(false)
    }

    Box (
        modifier = Modifier
            .padding(start = 32.dp)
            .fillMaxWidth()
    ) {
        SmallFloatingActionButton(onClick = {
            clicked = true
        }, modifier = Modifier.align(Alignment.Center) ) {
            Icon(Icons.TwoTone.KeyboardArrowDown, contentDescription = null)
        }
    }

    LaunchedEffect(clicked){
        stateProvider().animateScrollToItem(viewModel.getMsgCount())
        clicked = false
    }
}



@Composable
fun Conversation(
    paddingValues: PaddingValues,
    viewModel: ChatViewModel,
    stateProvider: () -> LazyListState,
    data: Data
){

    var replyArrived by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn (
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    paddingValues.calculateTopPadding(),
                    end = 16.dp,
                    paddingValues.calculateBottomPadding()
                )
                .weight(1f),
            state = stateProvider()
        ) {

//            items((1..50).toList()){
//                MessageBubbleOut(text = "Ciao come va")
//            }

            items(viewModel.msgList) {chatMessage ->
                if(chatMessage.isSent) {
                    BubbleOut(chatMessage = chatMessage)
                } else {
                    ReplyBubble(chatMessage = chatMessage)
                }
            }
        }


        UserInput(viewModel = viewModel, stateProvider = stateProvider, data = data)
    }


}



@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


@Composable
fun resolveText(input: String) {

    val arrayList: ArrayList<TextMessage> = TextResolver.resolveText(input)
    var annotatedString = AnnotatedString("")
    val n = count(arrayList)

    Box (
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
            )
    ) {
        Column {
            
        }
    }

}

fun count(list : ArrayList<TextMessage>) : Int{

    var num = 0

    for(item in list)
        if(item.code == TextCode.CODE)
            num++

    return list.size - num
}

@Composable
fun toAnnotatedString(list: ArrayList<TextMessage>) : AnnotatedString{

    val annotatedList : ArrayList<AnnotatedString>
    val customString = buildAnnotatedString {

        for(item in list){
            if(item.code == TextCode.PLAIN_TEXT){
                append(item.text)
            }else if(item.code == TextCode.CODE_LINE){
                withStyle(style = SpanStyle(
                    background = MaterialTheme.colorScheme.surface,
                    color = MaterialTheme.colorScheme.onSurface
                    )
                ){
                    append(item.text)
                }
            }else if(item.code == TextCode.CODE){
                break
            }
        }
    }

    return customString
}


@Composable
fun CodeStyle(text: String){

    val scroll = rememberScrollState(0)

    SelectionContainer {
        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .background(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
                .horizontalScroll(scroll),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
    }

}

@Composable
fun NormalTextStyle(text: String){

    Text(
        text = text,
        modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)

    )
}


@Preview
@Composable
fun GoBottom(){

    val density = LocalDensity.current


    FloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(Icons.TwoTone.KeyboardArrowDown, null)
    }
}




@Composable
fun LoadingReply(){

    Box {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 4.dp, end = 4.dp)
        )
    }
}


@Preview
@Composable
fun ResolveTextPreview(){

    DroidGPTTheme {
        resolveText(input = "Questo é un esempio di codice `python`:\n```print(\"hello\")```\nCiao!")
    }
}

@Preview
@Composable
fun ScreenPreview(){

    val navController = rememberNavController()
    val context = LocalContext.current
    val data = Data(context)
    val viewModel : ChatViewModel = viewModel()

    DroidGPTTheme {
        ScaffoldTest(navController = navController, data = data, viewModel = viewModel)
    }
}



@Preview
@Composable
fun CodeStylePreview(){
    DroidGPTTheme {
        CodeStyle(text = "python\nprint(\"Hello!\")\nprint(\"world\")")
    }
}

