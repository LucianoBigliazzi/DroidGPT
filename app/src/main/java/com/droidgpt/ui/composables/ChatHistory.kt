package com.droidgpt.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.navigation.NavHostController
import com.droidgpt.R
import com.droidgpt.data.Data
import com.droidgpt.viewmodel.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistory(navController: NavHostController, viewModel: ChatViewModel, data: Data){

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
            ChatHistoryContent(paddingValues, viewModel, data)
        }
    )
}

@Composable
fun ChatHistoryContent(paddingValues: PaddingValues, viewModel: ChatViewModel, data: Data) {

    val context = LocalContext.current


    Box (
        modifier = Modifier.padding(paddingValues = paddingValues).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Not yet implemented", style = MaterialTheme.typography.labelLarge, fontStyle = FontStyle.Italic)
    }

//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(
//                start = 20.dp,
//                top = paddingValues.calculateTopPadding(),
//                end = 20.dp,
//                bottom = paddingValues.calculateBottomPadding()
//            )
//    ){
//
//        items(viewModel.conversationList){
//            Text(text = it.drop(it.indexOf("\"", 119)).take(6))
//        }
//    }
}


fun getFirstMessageFromJson(item: String): String {

    return item
}