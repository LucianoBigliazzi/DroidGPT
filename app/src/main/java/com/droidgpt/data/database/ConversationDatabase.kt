package com.droidgpt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Conversation::class],
    version = 1
)
@TypeConverters(ConversationTypeConverter::class)
abstract class ConversationDatabase : RoomDatabase() {

    abstract val dao : ConversationDao
}