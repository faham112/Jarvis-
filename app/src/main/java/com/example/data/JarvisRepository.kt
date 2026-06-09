package com.example.data

import kotlinx.coroutines.flow.Flow

class JarvisRepository(private val dao: JarvisDao) {

    val allVoiceCommands: Flow<List<VoiceCommandEntity>> = dao.getAllVoiceCommands()
    val allIotDevices: Flow<List<IotDeviceEntity>> = dao.getAllIotDevices()
    val allPlugins: Flow<List<PluginEntity>> = dao.getAllPlugins()

    suspend fun insertVoiceCommand(command: VoiceCommandEntity) {
        dao.insertVoiceCommand(command)
    }

    suspend fun clearAllVoiceCommands() {
        dao.clearVoiceCommands()
    }

    suspend fun insertIotDevice(device: IotDeviceEntity) {
        dao.insertIotDevice(device)
    }

    suspend fun updateIotDevice(device: IotDeviceEntity) {
        dao.updateIotDevice(device)
    }

    suspend fun getIotDeviceById(id: String): IotDeviceEntity? {
        return dao.getIotDeviceById(id)
    }

    suspend fun insertPlugin(plugin: PluginEntity) {
        dao.insertPlugin(plugin)
    }

    suspend fun updatePlugin(plugin: PluginEntity) {
        dao.updatePlugin(plugin)
    }

    suspend fun deletePlugin(pluginId: String) {
        dao.deletePlugin(pluginId)
    }
}
