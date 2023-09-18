package com.droidgpt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertConversation(conversation: Conversation)

    @Delete
    suspend fun deleteConversation(conversation: Conversation)

    @Query("SELECT * FROM conversation")
    fun getAllConversations() : Flow<List<Conversation>>

    @Update(entity = Conversation::class)
    suspend fun updateTitle(title: ConversationUpdate)

}