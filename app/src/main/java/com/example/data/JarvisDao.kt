package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JarvisDao {

    // --- Voice Command Logs ---
    @Query("SELECT * FROM voice_commands ORDER BY timestamp DESC")
    fun getAllVoiceCommands(): Flow<List<VoiceCommandEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceCommand(command: VoiceCommandEntity)

    @Query("DELETE FROM voice_commands")
    suspend fun clearVoiceCommands()

    // --- IoT Home Devices ---
    @Query("SELECT * FROM iot_devices ORDER BY lastUpdated DESC")
    fun getAllIotDevices(): Flow<List<IotDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIotDevice(device: IotDeviceEntity)

    @Update
    suspend fun updateIotDevice(device: IotDeviceEntity)

    @Query("SELECT * FROM iot_devices WHERE deviceId = :id LIMIT 1")
    suspend fun getIotDeviceById(id: String): IotDeviceEntity?

    // --- Third-Party Plugins ---
    @Query("SELECT * FROM plugins")
    fun getAllPlugins(): Flow<List<PluginEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlugin(plugin: PluginEntity)

    @Update
    suspend fun updatePlugin(plugin: PluginEntity)

    @Query("DELETE FROM plugins WHERE pluginId = :pluginId")
    suspend fun deletePlugin(pluginId: String)
}
