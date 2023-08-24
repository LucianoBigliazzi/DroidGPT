package com.droidgpt.model

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.ViewModel
import com.droidgpt.data.Data
import com.droidgpt.data.labels.DataLabels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private var key = mutableStateOf("")
    var msgList = mutableStateListOf<ChatMessage>()
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
        addElement(ChatMessage(ApiReply(question, false), true, timeOut))
        //Loading bubble:
        addElement(ChatMessage(null, false, timeOut))

        viewModelScope.launch {
            data.experimentalInterrogateAPI(question, false){value ->
                val timeIn = System.currentTimeMillis()
                removeLoading()
                addElement(ChatMessage(value, false, timeIn))
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

    fun retrieveList(string: String){

        conversationList.clear()
        val parts = string.trim('[', ']').split(",")
        conversationList = parts.map { it.trim() } as SnapshotStateList<String>
    }


    fun clearList(){
        msgList.clear()
        msgCount = 0
        clearChatButton.value = false
    }

    fun addElement(msg : ChatMessage){
        msgList.add(msg)
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
        msgList.removeAt(msgCount - 1)
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

