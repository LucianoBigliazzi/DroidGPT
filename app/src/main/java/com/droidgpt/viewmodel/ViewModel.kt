package com.droidgpt.viewmodel

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.ViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.ProxyConfig
import com.droidgpt.data.Data
import com.droidgpt.data.labels.DataLabels
import com.droidgpt.data.labels.SettingsLabels
import com.droidgpt.model.MessageData
import com.droidgpt.ui.common.performHapticFeedbackIfEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

class ChatViewModel(data: Data) : ViewModel() {

    private var key = mutableStateOf("")
    private var msgCount = 0
    var connectionEstablished = false
    var darkTheme = mutableStateOf(false)
    var highContrast = mutableStateOf(false)
    private var temperature = mutableStateOf("0.7")
    private var chatTitle = mutableStateOf("DroidGPT")
    private var generateCompletion = mutableStateOf(false)
    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val loading = mutableStateOf(false)
    private val systemTheme = mutableStateOf(true)
    private var conversationList = mutableStateListOf<String>()
    private var clearChatButton = mutableStateOf(false)
    var data : Data
    private var openAI : OpenAI
    private var config : OpenAIConfig
    var libraryMsgList = mutableStateListOf<MessageData>()
    private val completionFlow : CompletionFlow
    var stream = mutableStateOf(true)
    var isHapticEnabled = mutableStateOf(true)

    init {
        this.data = data
        key.value = data.resolveKeyShared()
        this.config = OpenAIConfig(
            token = key.value,
            timeout = Timeout(socket = 120.seconds)
        )
        openAI = OpenAI(config = config)
        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.System, content = data.getFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR)), null))
        this.completionFlow = CompletionFlow(openAI)
    }

   // var completionList = mutableStateListOf<ChatCompletion>()


    suspend fun apiCallUsingLibrary(input: String): ChatCompletion {

        loading.value = true

        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.User, content = input), LocalDateTime.now()))

        startLoadingBubble()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = libraryMsgList.map { it.chatMessage },
            temperature = temperature.value.toDouble()
        )

        val completion = openAI.chatCompletion(chatCompletionRequest)

        stopLoadingBubble()

        //completionList.add(completion)
        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.Assistant, content = completion.choices[0].message?.content), LocalDateTime.now()))

        loading.value = false

        return completion
    }

    suspend fun chunkCompletion(input: String, haptic: HapticFeedback) {

        loading.value = true

        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.User, content = input), LocalDateTime.now()))

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = libraryMsgList.map { it.chatMessage },
            temperature = temperature.value.toDouble()
        )

        val text : StringBuilder = StringBuilder("")
        val textChunk : StringBuilder = StringBuilder("")
        val progressiveString : StringBuilder = StringBuilder("")

        println("-------------- COMPLETIONS ---------------")

        openAI.chatCompletions(chatCompletionRequest).collect { chunk ->
            textChunk.clear()
            textChunk.append(chunk.choices[0].delta.content.toString())
            text.append(textChunk)
            println(text)

            performHapticFeedbackIfEnabled(haptic, isHapticEnabled.value, HapticFeedbackType.TextHandleMove)


            for(c in textChunk){
                delay(5)
                progressiveString.append(c)
                if(libraryMsgList.last().chatMessage.role != ChatRole.Assistant){
                    libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.Assistant, content = progressiveString.toString()), LocalDateTime.now()))
                }else if(text.length > 3 && text.substring(text.length - 4) != "null") {
                    libraryMsgList.removeLast()
                    libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.Assistant, content = progressiveString.toString()), LocalDateTime.now()))
                }
            }



        }

        loading.value = false

        performHapticFeedbackIfEnabled(haptic, isHapticEnabled.value, HapticFeedbackType.LongPress)

    }


    private fun startLoadingBubble(){
        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.Function, content = "", name = "loading"), null))
    }

    private fun stopLoadingBubble(){
        libraryMsgList.removeLast()
    }

    fun setSystemMessage(text : String){
        data.saveStringToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR, text)
        libraryMsgList.clear()
        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.System, content = text), null))
    }

    fun changeAPIKey(key : String){
        this.key.value = key
        data.saveStringToSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.API_KEY, key)
    }

    // DEPRECATED
    fun performApiCall(
        question: String,
        data: Data,
        context: Context,
        haptic: HapticFeedback,
        view: View
    ){

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        view.playSoundEffect(SoundEffectConstants.CLICK)

        val timeOut = System.currentTimeMillis()
        //User message bubble:
        //addElement(ChatMessage(ApiReply(question, false), true, timeOut))
        //Loading bubble:
        //addElement(ChatMessage(null, false, timeOut))

        viewModelScope.launch {
            data.experimentalInterrogateAPI(question, false){value ->
                val timeIn = System.currentTimeMillis()
                removeLoading()
                //addElement(ChatMessage(value, false, timeIn))
                data.addAnswer(context, value.text)
                println(data.getJsonString(context))
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                loading.value = false
            }
        }
    }

    fun addElementToHistory(string: String, context: Context){
        conversationList.add(string)
        saveConversationList(context = context)
    }

    private fun saveConversationList(context: Context){
        val sharedPreferences = context.getSharedPreferences(DataLabels.DATA, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(DataLabels.HISTORY, conversationList.toString()).apply()
    }


    @OptIn(BetaOpenAI::class)
    fun clearList(){
        libraryMsgList.clear()
        libraryMsgList.add(MessageData(ChatMessage(role = ChatRole.System, content = data.getFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR)), null))
        msgCount = 0
        clearChatButton.value = false
    }

    fun addElement(msg : ChatMessage){
        //msgList.add(msg)
        msgCount++
    }

    fun getKey() : String {
        return key.value
    }
    fun setKey(newKey : String){
        key.value = newKey
    }

    fun getMsgCount(): Int {
        return msgCount
    }

    fun removeLoading(){
        //msgList.removeAt(msgCount - 1)
        msgCount--
    }

    fun setDark(bool : Boolean){
        darkTheme.value = bool
    }

    fun setHighContrast(bool : Boolean){
        highContrast.value = bool
    }

    fun isDarkTheme() : Boolean {
        return darkTheme.value
    }

    fun isHighContrast() : Boolean {
        return highContrast.value
    }

    fun setTemperature(n : String){
        temperature.value = n
    }

    fun getTemperature() : String {
        return temperature.value
    }

    fun getChatTitle() : String {
        return chatTitle.value
    }

    fun setChatTitle(title : String){
        chatTitle.value = title
    }

    fun getGenerateCompletion() : Boolean {
        return generateCompletion.value
    }

    fun setGenerateCompletion(check : Boolean){
        generateCompletion.value = check
    }

    fun isLoading() : Boolean{
        return loading.value
    }

    fun setLoading(check : Boolean){
        loading.value = check
    }

    fun isSystemTheme() : Boolean{
        return systemTheme.value
    }

    fun setSystemTheme(check : Boolean){
        systemTheme.value = check
    }

    fun setClearChatButtonVisibility(check : Boolean){
        clearChatButton.value = true
    }

    fun isClearChatButtonVisible() : Boolean{
        return clearChatButton.value
    }

    fun getHistoryList() : List<String>{
        return conversationList.toList()
    }
}

