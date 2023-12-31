package com.droidgpt.ui.composables

import android.content.ClipData
import android.content.ClipboardManager
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.droidgpt.R
import com.droidgpt.data.Data
import com.droidgpt.data.KeyManager
import com.droidgpt.data.labels.SettingsLabels
import com.droidgpt.viewmodel.ChatViewModel
import com.droidgpt.ui.common.ChangeApiKeyDialog
import com.droidgpt.ui.common.ShowBehaviourDialog
import com.droidgpt.ui.common.TemperatureDialog
import com.droidgpt.ui.common.performHapticFeedbackIfEnabled
import com.droidgpt.ui.image
import com.droidgpt.ui.theme.DroidGPTTheme
import com.droidgpt.ui.theme.parseSurfaceColor
import kotlinx.coroutines.delay


@Composable
fun SettingsScreen(
    navController: NavHostController,
    window: Window,
    data: Data,
    viewModel: ChatViewModel
){

    DroidGPTTheme (
        darkTheme = if(viewModel.isSystemTheme()) isSystemInDarkTheme() else viewModel.isDarkTheme(),
        isHighContrastModeEnabled = viewModel.isHighContrast(),
        dynamicColor = viewModel.dynamic.value
    ) {
        SettingsScaffold(navController, data, viewModel)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScaffold(navController: NavHostController, data: Data, viewModel: ChatViewModel) {

    var pop by remember {
        mutableStateOf(false)
    }

    Scaffold (
        topBar = { LargeTopAppBar(title = { Text(text = "Settings", style = MaterialTheme.typography.displaySmall) },
            navigationIcon = {
                IconButton(onClick = { pop = true }) {
                    Icon(Icons.TwoTone.ArrowBack, null)
                }
            },
            colors = topAppBarColors(containerColor = parseSurfaceColor(highContrast = viewModel.highContrast.value)))
        },
        content = { paddingValues -> SettingsContent(paddingValues, data, viewModel) },
        containerColor = parseSurfaceColor(highContrast = viewModel.highContrast.value)
    )

    LaunchedEffect(pop){
        if(pop){
            delay(50)
            pop = false
            navController.popBackStack()
        }
    }


}


@Composable
fun SettingsContent(
    paddingValues: PaddingValues,
    data: Data,
    viewModel: ChatViewModel
) {

    var showTemperatureDialog by remember {
        mutableStateOf(false)
    }
    var showChangeApiKeyDialog by remember {
        mutableStateOf(false)
    }
    var showBehaviourDialog by remember {
        mutableStateOf(false)
    }

    var temperatureValue by remember {
        mutableFloatStateOf(0.7F)
    }
    var newKey by remember {
        mutableStateOf("")
    }
    var gpt4 by remember {
        mutableStateOf(false)
    }
    val enginePreferencesText by remember {
        mutableStateOf("Engine preferences")
    }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val isSystemInDarkTheme = isSystemInDarkTheme()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .background(color = parseSurfaceColor(highContrast = viewModel.highContrast.value))
    ) {

        SettingsParagraphTitle(title = enginePreferencesText)

        DefaultSettingsItem(
            title = "Temperature",
            subtitle = "Set a creativity meter",
            icon = painterResource(id = R.drawable.ic_device_thermostat)
        ) { showTemperatureDialog = true }

        DefaultSettingsItem(
            title = "API key",
            subtitle = "Change API key",
            icon = painterResource(id = R.drawable.key),
            onClick = { showChangeApiKeyDialog = true }
        )

        DefaultSettingsItem(
            title = "System message",
            subtitle = "Change the initial behaviour",
            icon = painterResource(id = R.drawable.stream_apps),
            onClick = { showBehaviourDialog = true })

        SwitchSettingsItem(
            title = "Stream",
            subtitle = "Progressively show incoming completion",
            icon = Icons.Filled.PlayArrow,
            onCheck = {
                viewModel.stream.value = it
                data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.STREAM, it)
            },
            check = viewModel.stream.value
        )

        SettingsParagraphTitle(title = "General")

        SwitchSettingsItem(
            title = "Haptic feedback",
            subtitle = "Enable vibration when action is performed",
            icon = painterResource(id = R.drawable.vibration),
            onCheck = {
                viewModel.isHapticEnabled.value = it
                data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HAPTIC, it)
            },
            check = viewModel.isHapticEnabled.value
        )


        SettingsParagraphTitle(title = "Theme")

        SwitchSettingsItem(
            title = "Material You",
            subtitle = "Activate dynamic colors based on your wallpaper",
            icon = Icons.Filled.FavoriteBorder,
            onCheck = {
                viewModel.dynamic.value = it
                data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DYNAMIC, it)
            },
            check = viewModel.dynamic.value
        )

        SwitchSettingsItem(
            title = "System theme",
            subtitle = "Follow current system theme",
            icon = painterResource(id = R.drawable.night_sight_auto),
            onCheck = {
                if(it){
                    viewModel.setSystemTheme(true)
                    data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.SYSTEM_THEME, true)
                    if(isSystemInDarkTheme){
                        viewModel.setDark(true)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, true)
                    } else if(viewModel.isDarkTheme() && viewModel.isHighContrast()){
                        viewModel.setDark(false)
                        viewModel.setHighContrast(false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, false)
                    } else {
                        viewModel.setDark(false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, false)
                    }
                }else{
                    viewModel.setSystemTheme(false)
                    data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.SYSTEM_THEME, false)
                }
            },
            check = viewModel.isSystemTheme()
        )

        if(!viewModel.isSystemTheme()){
            SwitchSettingsItem(
                title = "Dark theme",
                subtitle = "Switch to default dark theme",
                icon = painterResource(id = R.drawable.dark_mode),
                onCheck = {
                    if(!it && viewModel.isHighContrast()){
                        viewModel.setDark(false)
                        viewModel.setHighContrast(false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, false)
                    } else {
                        viewModel.setDark(it)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, it)
                    }
                },
                check = viewModel.isDarkTheme()
            )
        }

        if(!viewModel.isSystemTheme() || (viewModel.isSystemTheme() && isSystemInDarkTheme()) ){
            SwitchSettingsItem(
                title = "Pure black",
                subtitle = "High contrast background",
                icon = painterResource(id = R.drawable.contrast),
                onCheck = {
                    if(viewModel.isDarkTheme() && it){
                        viewModel.setHighContrast(true)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, true)
                    }else if(!viewModel.isDarkTheme() && it){
                        viewModel.setDark(true)
                        viewModel.setHighContrast(true)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, true)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, true)
                    }else if(!it && viewModel.isDarkTheme()){
                        viewModel.setHighContrast(false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, false)
                    }else if(!it && !viewModel.isDarkTheme()){
                        viewModel.setHighContrast(false)
                        viewModel.setDark(true)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.HIGH_CONTRAST, false)
                        data.saveBooleanToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.DARK_THEME, true)
                    }
                },
                check = viewModel.isHighContrast()
            )
        }


        SettingsParagraphTitle(title = "About")
        About(
            modifier = Modifier.padding(20.dp, 1.dp, 20.dp, 1.dp),
            isHapticEnabled = viewModel.isHapticEnabled.value
        )
    }


    if(showBehaviourDialog){

        ShowBehaviourDialog(
            onDismiss = { showBehaviourDialog = false },
            onConfirm = { value ->
                showBehaviourDialog = false
                if(value.trim().isNotBlank()){
                    data.saveStringToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR, value)
                    viewModel.setSystemMessage(value)
                    //clearChat(viewModel, context, data) Unused with new library
                    Toast.makeText(context, "Custom behaviour set", Toast.LENGTH_SHORT).show()
                } else {
                    data.saveStringToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR, "You are a helpful assistant")
                    Toast.makeText(context, "Default behaviour set", Toast.LENGTH_SHORT).show()
                }
            },
            currentSysMessage = data.behaviourFromSharedPreferences)
    }


    if(showChangeApiKeyDialog){
        ChangeApiKeyDialog(
            onConfirm = { value ->
                showChangeApiKeyDialog = false
                if(KeyManager.validateKey(value)) {
                    viewModel.changeAPIKey(value)
                    Toast.makeText(context, "New key accepted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Key unchanged: invalid", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { showChangeApiKeyDialog = false },
        )
    }

    if(showTemperatureDialog){
        TemperatureDialog(
            onDismiss = { showTemperatureDialog = false }
        ) { value ->
            viewModel.setTemperature(value.toString().take(3))
            println(viewModel.getTemperature())
            data.saveFloatToSharedPreferences(
                SettingsLabels.SETTINGS,
                SettingsLabels.TEMPERATURE,
                viewModel.getTemperature().toFloat()
            )
            viewModel.clearList()
            Toast.makeText(
                context,
                "Temperature set to " + viewModel.getTemperature(),
                Toast.LENGTH_SHORT
            ).show()
            showTemperatureDialog = false
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun About(modifier: Modifier, isHapticEnabled: Boolean){

    val context = LocalContext.current
    val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    val secret = stringResource(id = R.string.secret_code_sha256)
    val haptic = LocalHapticFeedback.current

    Text(
        text = stringResource(id = R.string.app_name),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall
    )

    Text(
        text = "Version: " + stringResource(id = R.string.version),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall
    )

    Text(
        text = secret,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall, fontSize = 11.sp,
        modifier = modifier.combinedClickable(
            enabled = true,
            onLongClickLabel = "about long click",
            onLongClick = {
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("secret code", secret))
                    performHapticFeedbackIfEnabled(haptic, isHapticEnabled, HapticFeedbackType.LongPress)
                }
            },
            onClick = {},
            onDoubleClick = {}
        ),
    )
}



@Preview
@Composable
fun SettingsScreenPreview(){

    val navController = rememberNavController()
    val data = Data(LocalContext.current)
    val viewModel : ChatViewModel = viewModel()

    DroidGPTTheme (darkTheme = true) {
        SettingsScaffold(navController = navController, data = data, viewModel = viewModel)
    }
}


@Composable
fun DefaultSettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
){

    TextButton(onClick = onClick) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {

            Icon(icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )

            //Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun DefaultSettingsItem(
    title: String,
    subtitle: String,
    icon: Painter,
    onClick: () -> Unit,
){

    TextButton(onClick = onClick) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 0.dp),
        ) {

            Icon(icon, null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )

            //Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultSettingsItemPreview(){

    DroidGPTTheme {
        DefaultSettingsItem(
            "Title",
            "subtitle",
            icon = Icons.TwoTone.Warning
        ) {}
    }
}


@Composable
fun SwitchSettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onCheck: (Boolean) -> Unit,
    check: Boolean
){


    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 4.dp),
    ) {

        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        //Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            Switch(
                checked = check,
                onCheckedChange = onCheck,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }

    }
}



@Composable
fun SwitchSettingsItem(
    title: String,
    subtitle: String,
    icon: Painter,
    onCheck: (Boolean) -> Unit,
    check: Boolean
){


    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
    ) {

        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        //Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            Switch(
                checked = check,
                onCheckedChange = onCheck,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun CheckBoxSettingsItemPreview(){

    DroidGPTTheme {
        SwitchSettingsItem(
            "Title",
            "subtitle",
            icon = Icons.TwoTone.Warning,
            onCheck = {},
            check = true
        )
    }
}

@Composable
fun SettingsParagraphTitle(title: String){

    Text(
        text = title,
        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 16.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        //fontWeight = FontWeight.Normal
    )
}