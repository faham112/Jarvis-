package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plugins")
data class PluginEntity(
    @PrimaryKey val pluginId: String,
    val name: String,
    val version: String,
    val description: String,
    val isEnabled: Boolean = false,
    val permissionsRequested: String, // Stringified CSV e.g., "SYSTEM,INTERNET,IOT_CONTROL"
    val scriptSnippet: String // Safe local javascript-like or json rules for modular extension
)
