package com.droidgpt.viewmodel

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.ViewModel
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.droidgpt.data.database.Conversation
import com.droidgpt.data.database.ConversationDao
import com.droidgpt.data.database.ConversationEvent
import com.droidgpt.data.database.ConversationState
import com.droidgpt.data.Data
import com.droidgpt.data.database.SortType
import com.droidgpt.data.labels.DataLabels
import com.droidgpt.data.labels.SettingsLabels
import com.droidgpt.model.MessageData
import com.droidgpt.ui.common.performHapticFeedbackIfEnabled
import com.droidgpt.ui.composables.takeTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(data: Data, conversationDao: ConversationDao) : ViewModel() {

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
    var stream = mutableStateOf(true)
    val dynamic = mutableStateOf(true)
    var isHapticEnabled = mutableStateOf(true)
    val conversationDao : ConversationDao
    private val conversationsList = mutableStateListOf<Conversation>()

    init {
        this.data = data
        key.value = data.resolveKeyShared()
        this.config = OpenAIConfig(
            token = key.value,
            timeout = Timeout(socket = 120.seconds)
        )
        openAI = OpenAI(config = config)
        addToLibraryList(MessageData(ChatMessage(role = ChatRole.System, content = data.getFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR)), LocalDateTime.now()))
        this.conversationDao = conversationDao
    }

    //val allConversations : LiveData<List<Conversation>> = repository.allConversations.asLiveData()

    // Database
    private val _sortType = MutableStateFlow(SortType.ID)
    private val _conversations = _sortType
        .flatMapLatest { sortType ->
            when(sortType){
                SortType.ID -> conversationDao.getAllConversations()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ConversationState())

    val state = combine(_state, _sortType, _conversations) { state, sortType, conversations ->
        state.copy(
            conversations = conversations,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConversationState())


    fun updateConversationsList(conversation: Conversation){
        conversationsList.add(conversation)
    }


    fun onEvent(event: ConversationEvent){
        when(event){
            ConversationEvent.SaveConversation -> {
                val creationDate    = state.value.creationDate
                val title           = state.value.title
                //val id              = state.value.id
                val messageDataList = state.value.messageDataList

                if(messageDataList.isEmpty() || title.isBlank())
                    return

                val conversation = Conversation(
                    creationDate = creationDate,
                    title = title,
                    //id = id,
                    messagesList = messageDataList
                )

                viewModelScope.launch {
                    conversationDao.upsertConversation(conversation = conversation)
                }

                _state.update {
                    it.copy(
                        creationDate = LocalDate.now(),
                        title = "",
                        messageDataList = emptyList()
                    )
                }

            }
            is ConversationEvent.DeleteConversation -> {
                viewModelScope.launch {
                    conversationDao.deleteConversation(conversation = event.conversation)
                }
            }

            is ConversationEvent.SetCreationDate -> {
                _state.update { it.copy(creationDate = event.localDate) }
            }
            is ConversationEvent.SetTitle -> {
                _state.update { it.copy(title = event.title) }
            }
            is ConversationEvent.SetID -> {
                //_state.update { it.copy(id = event.id) }
            }
            is ConversationEvent.SetMessageDataList -> {
                _state.update { it.copy(messageDataList = event.messageDataList) }
            }

            is ConversationEvent.SortConversations -> {
                _sortType.value = event.sortType
            }
        }
    }

    fun addToLibraryList(messageData: MessageData){
        libraryMsgList.add(messageData)
        //onEvent(ConversationEvent.SetMessageDataList(libraryMsgList.toList()))
    }

    suspend fun generateTitle() : String {

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = libraryMsgList.map { it.chatMessage },
            temperature = 0.1
        )

        val completion = openAI.chatCompletion(chatCompletionRequest)

        return completion.choices[0].message.content.toString()
    }

    suspend fun apiCallUsingLibrary(input: String): ChatCompletion {

        loading.value = true

        val messageData = MessageData(ChatMessage(role = ChatRole.User, content = input), LocalDateTime.now())

        onEvent(ConversationEvent.SetCreationDate(LocalDate.now()))

        addToLibraryList(messageData)

        val singleMessageRange = 4  // if list size > 4 then don't make a new title
        // Idk why i need to set the title even if I continue a previous conversation
        if(libraryMsgList.size < singleMessageRange) {
            onEvent(ConversationEvent.SetCreationDate(LocalDate.now()))
            onEvent(ConversationEvent.SetTitle(takeTitle(messageData.chatMessage.content.toString())))
        }
        else {
            onEvent(ConversationEvent.SetCreationDate(libraryMsgList.toList()[1].messageTime.toLocalDate()))
            onEvent(ConversationEvent.SetTitle(takeTitle(libraryMsgList.toList()[1].chatMessage.content.toString())))
        }

        startLoadingBubble()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = libraryMsgList.map { it.chatMessage },
            temperature = temperature.value.toDouble()
        )

        val completion = openAI.chatCompletion(chatCompletionRequest)

        stopLoadingBubble()

        //completionList.add(completion)
        addToLibraryList(MessageData(ChatMessage(role = ChatRole.Assistant, content = completion.choices[0].message?.content), LocalDateTime.now()))

        loading.value = false

        return completion
    }

    suspend fun chunkCompletion(input: String, haptic: HapticFeedback) {

        loading.value = true

        val messageData = MessageData(ChatMessage(role = ChatRole.User, content = input), LocalDateTime.now())

        onEvent(ConversationEvent.SetCreationDate(LocalDate.now()))

        addToLibraryList(messageData)

        val singleMessageRange = 4  // if list size > 4 then don't make a new title
        // Idk why i need to set the title even if I continue a previous conversation
        if(libraryMsgList.size < singleMessageRange) {
            onEvent(ConversationEvent.SetCreationDate(LocalDate.now()))
            onEvent(ConversationEvent.SetTitle(takeTitle(messageData.chatMessage.content.toString())))
        }
        else {
            onEvent(ConversationEvent.SetCreationDate(libraryMsgList.toList()[1].messageTime.toLocalDate()))
            onEvent(ConversationEvent.SetTitle(takeTitle(libraryMsgList.toList()[1].chatMessage.content.toString())))
        }


        onEvent(ConversationEvent.SaveConversation)


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

        onEvent(ConversationEvent.SetMessageDataList(libraryMsgList.toList()))

        //onEvent(ConversationEvent.SaveConversation)

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
        addToLibraryList(MessageData(ChatMessage(role = ChatRole.System, content = text), LocalDateTime.now()))
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


    fun clearList(){
        libraryMsgList.clear()
        addToLibraryList(MessageData(ChatMessage(role = ChatRole.System, content = data.getFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.BEHAVIOUR)), LocalDateTime.now()))
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

