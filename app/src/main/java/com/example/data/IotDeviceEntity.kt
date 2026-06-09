package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "iot_devices")
data class IotDeviceEntity(
    @PrimaryKey val deviceId: String,
    val name: String,
    val category: String, // LIGHT, TEMPERATURE, LOCK, ALARM
    val encryptedState: String, // Encrypted string representation of current state (e.g. "ON:80%", "72F", "LOCKED")
    val hardwareTriggerProtected: Boolean = true, // Priority encryption check
    val lastUpdated: Long = System.currentTimeMillis()
)
