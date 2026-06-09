package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "voice_commands")
data class VoiceCommandEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val encryptedText: String,   // Encrypted text query
    val encryptedAction: String, // Encrypted parsed system intent/result
    val timestamp: Long = System.currentTimeMillis(),
    val isOffline: Boolean = true,
    val isSuccess: Boolean = true
)
