package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.JarvisCryptography
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.spec.SecretKeySpec

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val database = JarvisDatabase.getDatabase(application)
    private val repository = JarvisRepository(database.jarvisDao())
    private val geminiRepository = GeminiRepository()

    // --- State Toggles & Configurations ---
    val isOfflineMode = MutableStateFlow(true)
    val encryptionKeyPhrase = MutableStateFlow("MARK-IV-SECURE-STATION")
    
    // Voice execution status
    val executionLog = MutableStateFlow<String>("Awaiting trigger sequence...")
    val voiceTranscript = MutableStateFlow("")
    val isListening = MutableStateFlow(false)

    // --- Simulated Active Device Parametric Telemetry (Real-time data analysis) ---
    val batteryLevel = MutableStateFlow(84)
    val ramState = MutableStateFlow("3.2 GB / 8.0 GB")
    val cpuUtilization = MutableStateFlow(14)
    val tempSensor = MutableStateFlow(36.1f) // Celsius

    // --- Local Physical Device Settings (Simulates Device Control Action) ---
    val deviceVolume = MutableStateFlow(60) // 0-100
    val deviceBrightness = MutableStateFlow(75) // 0-100
    val isWifiOn = MutableStateFlow(true)
    val isBluetoothOn = MutableStateFlow(false)

    // --- Decryption inspector state for user education ---
    val selectedEncryptedLogId = MutableStateFlow<String?>(null)
    val decryptedLogText = MutableStateFlow<String?>(null)

    // --- Dynamic database flows ---
    val voiceCommands = repository.allVoiceCommands.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val iotDevices = repository.allIotDevices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val plugins = repository.allPlugins.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Pre-populate IoT accessories and plugins on startup if empty
        viewModelScope.launch {
            repository.allIotDevices.first().let { list ->
                if (list.isEmpty()) {
                    populateInitialAccess();
                }
            }
            repository.allPlugins.first().let { list ->
                if (list.isEmpty()) {
                    populateInitialPlugins();
                }
            }
            simulateLiveTelemetry()
        }
    }

    private fun getSecretKey(): SecretKeySpec {
        return JarvisCryptography.deriveKey(encryptionKeyPhrase.value)
    }

    private suspend fun populateInitialAccess() {
        val key = getSecretKey()
        val initialIot = listOf(
            IotDeviceEntity("living_light_1", "Cyber Light", "LIGHT", JarvisCryptography.encrypt("LIGHT_ON:80%", key), true),
            IotDeviceEntity("hvac_thermo_1", "Smart climate Core", "TEMPERATURE", JarvisCryptography.encrypt("TEMP:21.5C", key), true),
            IotDeviceEntity("perimeter_gate", "Perimeter Vault", "LOCK", JarvisCryptography.encrypt("VAL_LOCKED", key), true),
            IotDeviceEntity("core_defense", "Sentry Defense shield", "ALARM", JarvisCryptography.encrypt("DISARMED", key), true)
        )
        initialIot.forEach { repository.insertIotDevice(it) }
    }

    private suspend fun populateInitialPlugins() {
        val initialPlugins = listOf(
            PluginEntity("p_notifier", "Neural SMS Responder", "1.0.4", "Auto-response to phone notifications locally via secure triggers.", false, "SYSTEM,SMS_READ", "onNotificationReceived(notify) { encryptLog(notify); }"),
            PluginEntity("p_nest", "Nest-Link Node", "2.1.0", "Third party link to Nest thermostat offline grids securely.", true, "IOT_CONTROL,INTERNET", "onTempThreshold(t) { if (t > 25C) triggerCool(); }"),
            PluginEntity("p_audio_bridge", "Haptic Waveform Bridge", "0.8.1", "Secure audio spectral analysis of environmental patterns.", false, "AUDIO_INPUT", "analyzeFrequencies(f) { vibrateDevice(60); }")
        )
        initialPlugins.forEach { repository.insertPlugin(it) }
    }

    private fun simulateLiveTelemetry() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(4000)
                // Mutate telemetry values dynamically for visual real-time data analysis
                batteryLevel.value = (batteryLevel.value - 1).coerceAtLeast(1)
                cpuUtilization.value = Random().nextInt(15) + 5
                tempSensor.value = 35.0f + Random().nextFloat() * 2.5f
            }
        }
    }

    // --- Processing Command Inputs (Voice controls / Keyboard Inputs) ---
    fun submitQuery(query: String) {
        if (query.trim().isEmpty()) return
        
        viewModelScope.launch {
            executionLog.value = "Ingesting commands: '$query'..."
            val key = getSecretKey()
            val offline = isOfflineMode.value
            
            if (offline) {
                // LOCAL HEURISTIC PARSING ENGINE
                logLocalHeuristics(query, key)
            } else {
                // CLOUD AI ASSISTED DETAILED REAL-TIME PROCESS
                executionLog.value = "Streaming telemetry envelope to cloud core..."
                val result = geminiRepository.analyzeTelemetry(
                    "User command: $query. Current device states - Volume: ${deviceVolume.value}%, Brightness: ${deviceBrightness.value}%, Wi-Fi: ${if (isWifiOn.value) "On" else "Off"}. System tele - Cpu: ${cpuUtilization.value}%, Battery: ${batteryLevel.value}%. Respond brief, and state if you are updating any controls."
                )
                executionLog.value = result

                // Automatically try to extract actions even from Gemini outputs
                extractStateModifications(query)

                val encQuery = JarvisCryptography.encrypt(query, key)
                val encResult = JarvisCryptography.encrypt("AI RESPONSE: $result", key)
                val isSuccessAction = !result.startsWith("Error", true) && !result.startsWith("Grid Anomaly", true)
                repository.insertVoiceCommand(
                    VoiceCommandEntity(
                        encryptedText = encQuery,
                        encryptedAction = encResult,
                        isOffline = false,
                        isSuccess = isSuccessAction
                    )
                )
            }
        }
    }

    private suspend fun logLocalHeuristics(query: String, key: SecretKeySpec) {
        val lower = query.lowercase(Locale.ROOT)
        var actionText = "Executed Command Unrecognized - Routing to Offline Log"
        var succeeded = false

        when {
            // Volume Commands
            lower.contains("volume") || lower.contains("sound") -> {
                val match = Regex("\\d+").find(lower)
                val targetVol = match?.value?.toIntOrNull()
                if (targetVol != null) {
                    deviceVolume.value = targetVol.coerceIn(0, 100)
                    actionText = "System Vol adjusted to ${deviceVolume.value}%"
                } else if (lower.contains("up") || lower.contains("raise") || lower.contains("increase")) {
                    deviceVolume.value = (deviceVolume.value + 15).coerceAtMost(100)
                    actionText = "System Vol increased to ${deviceVolume.value}%"
                } else if (lower.contains("down") || lower.contains("lower") || lower.contains("decrease")) {
                    deviceVolume.value = (deviceVolume.value - 15).coerceAtLeast(0)
                    actionText = "System Vol decreased to ${deviceVolume.value}%"
                } else {
                    actionText = "System Volume state audited successfully."
                }
                succeeded = true
            }

            // Brightness Commands
            lower.contains("brightness") || lower.contains("screen") || lower.contains("display") -> {
                val match = Regex("\\d+").find(lower)
                val targetBright = match?.value?.toIntOrNull()
                if (targetBright != null) {
                    deviceBrightness.value = targetBright.coerceIn(0, 100)
                    actionText = "Display Brightness calibrated to ${deviceBrightness.value}%"
                } else if (lower.contains("up") || lower.contains("raise") || lower.contains("increase")) {
                    deviceBrightness.value = (deviceBrightness.value + 20).coerceAtMost(100)
                    actionText = "Display Brightness raised to ${deviceBrightness.value}%"
                } else if (lower.contains("down") || lower.contains("lower") || lower.contains("decrease")) {
                    deviceBrightness.value = (deviceBrightness.value - 20).coerceAtLeast(0)
                    actionText = "Display Brightness dimmed to ${deviceBrightness.value}%"
                } else {
                    actionText = "Display Brightness verified globally."
                }
                succeeded = true
            }

            // Wi-Fi or Bluetooth Connection Controls
            lower.contains("wifi") || lower.contains("internet") || lower.contains("network") -> {
                if (lower.contains("on") || lower.contains("enable") || lower.contains("start")) {
                    isWifiOn.value = true
                    actionText = "Local offline wireless module activated."
                } else if (lower.contains("off") || lower.contains("disable") || lower.contains("stop")) {
                    isWifiOn.value = false
                    actionText = "Local offline wireless module isolated."
                } else {
                    isWifiOn.value = !isWifiOn.value
                    actionText = "Wireless toggle processed. Active: ${isWifiOn.value}"
                }
                succeeded = true
            }

            lower.contains("bluetooth") || lower.contains("bt") || lower.contains("mesh") -> {
                if (lower.contains("on") || lower.contains("enable")) {
                    isBluetoothOn.value = true
                    actionText = "Bluetooth node active."
                } else if (lower.contains("off") || lower.contains("disable")) {
                    isBluetoothOn.value = false
                    actionText = "Bluetooth node isolated."
                } else {
                    isBluetoothOn.value = !isBluetoothOn.value
                    actionText = "Bluetooth link state altered. Sync status: ${isBluetoothOn.value}"
                }
                succeeded = true
            }

            // IoT Integrations
            lower.contains("light") || lower.contains("lamp") -> {
                val levelMatch = Regex("\\d+").find(lower)
                val target = levelMatch?.value?.toIntOrNull() ?: 100
                val encrypted = JarvisCryptography.encrypt(
                    if (lower.contains("off")) "LIGHT_OFF" else "LIGHT_ON:$target%",
                    key
                )
                repository.insertIotDevice(
                    IotDeviceEntity("living_light_1", "Cyber Light", "LIGHT", encrypted, true)
                )
                actionText = "Trigger cryptographically hashed and piped to Cyber Light: ON $target%"
                succeeded = true
            }

            lower.contains("gate") || lower.contains("door") || lower.contains("lock") -> {
                val status = if (lower.contains("unlock") || lower.contains("open")) "VAL_OPEN" else "VAL_LOCKED"
                val encrypted = JarvisCryptography.encrypt(status, key)
                repository.insertIotDevice(
                    IotDeviceEntity("perimeter_gate", "Perimeter Vault", "LOCK", encrypted, true)
                )
                actionText = "Triggers finalized & encrypted for Vault security node."
                succeeded = true
            }

            lower.contains("climate") || lower.contains("temp") || lower.contains("temperature") || lower.contains("hvac") -> {
                val tempMatch = Regex("\\d+\\.?\\d*").find(lower)
                val targetTemp = tempMatch?.value ?: "22.0"
                val encrypted = JarvisCryptography.encrypt("TEMP:${targetTemp}C", key)
                repository.insertIotDevice(
                    IotDeviceEntity("hvac_thermo_1", "Smart climate Core", "TEMPERATURE", encrypted, true)
                )
                actionText = "Climate control envelope set locally: TEMP ${targetTemp}°C"
                succeeded = true
            }

            lower.contains("defense") || lower.contains("shield") || lower.contains("sentry") || lower.contains("alarm") -> {
                val state = if (lower.contains("arm") || lower.contains("activate")) "ARMED" else "DISARMED"
                val encrypted = JarvisCryptography.encrypt(state, key)
                repository.insertIotDevice(
                    IotDeviceEntity("core_defense", "Sentry Defense shield", "ALARM", encrypted, true)
                )
                actionText = "Core security barriers refreshed: $state."
                succeeded = true
            }
        }

        executionLog.value = "[OFFLINE DISPATCH] $actionText"
        
        // Save encrypted log entry securely in Room
        val encQuery = JarvisCryptography.encrypt(query, key)
        val encAction = JarvisCryptography.encrypt(actionText, key)
        repository.insertVoiceCommand(
            VoiceCommandEntity(
                encryptedText = encQuery,
                encryptedAction = encAction,
                isOffline = true,
                isSuccess = succeeded
            )
        )
    }

    private fun extractStateModifications(query: String) {
        // Fallback state modifier for natural speech updates when operating online
        val lower = query.lowercase(Locale.ROOT)
        if (lower.contains("volume")) {
            val v = Regex("\\d+").find(lower)?.value?.toIntOrNull()
            if (v != null) deviceVolume.value = v.coerceIn(0, 100)
        }
        if (lower.contains("brightness")) {
            val b = Regex("\\d+").find(lower)?.value?.toIntOrNull()
            if (b != null) deviceBrightness.value = b.coerceIn(0, 100)
        }
        if (lower.contains("wifi")) {
            if (lower.contains("off")) isWifiOn.value = false
            if (lower.contains("on")) isWifiOn.value = true
        }
    }

    // --- Preset Quick Voice Trigger Command templates (Simulating Speech Actions) ---
    fun selectQuickVoiceAction(commandTemplate: String) {
        voiceTranscript.value = commandTemplate
        submitQuery(commandTemplate)
    }

    // --- Encryption/Decryption Live Visualizer ---
    fun selectLogForDecryption(entity: VoiceCommandEntity) {
        selectedEncryptedLogId.value = entity.id
        viewModelScope.launch {
            val key = getSecretKey()
            val decQ = JarvisCryptography.decrypt(entity.encryptedText, key)
            val decA = JarvisCryptography.decrypt(entity.encryptedAction, key)
            decryptedLogText.value = "Input Command: '$decQ'\nAction Output: '$decA'"
        }
    }

    fun closeDecryptionVisualizer() {
        selectedEncryptedLogId.value = null
        decryptedLogText.value = null
    }

    // --- Dev Plugins Actions ---
    fun togglePlugin(plugin: PluginEntity) {
        viewModelScope.launch {
            repository.updatePlugin(plugin.copy(isEnabled = !plugin.isEnabled))
        }
    }

    fun registerNewCustomPlugin(name: String, desc: String, script: String, perms: String) {
        val cleanName = name.trim().ifEmpty { return }
        val id = "p_custom_${UUID.randomUUID().toString().take(4)}"
        viewModelScope.launch {
            repository.insertPlugin(
                PluginEntity(
                    pluginId = id,
                    name = cleanName,
                    version = "1.0.0",
                    description = desc.trim().ifEmpty { "Community modular plugin" },
                    isEnabled = true,
                    permissionsRequested = perms.trim().ifEmpty { "USER_DEFINED" },
                    scriptSnippet = script.trim().ifEmpty { "void onTrigger() {}" }
                )
            )
        }
    }

    fun deletePlugin(plugin: PluginEntity) {
        viewModelScope.launch {
            repository.deletePlugin(plugin.pluginId)
        }
    }

    // --- Decrypted value getter for IoT device controls ---
    fun decryptIotState(encryptedState: String): String {
        return JarvisCryptography.decrypt(encryptedState, getSecretKey())
    }

    // Helper to change values manually too
    fun manualMuteDevice() {
        deviceVolume.value = 0
    }

    fun triggerSmartTriggerDirectly(device: IotDeviceEntity, targetPlainState: String) {
        viewModelScope.launch {
            val key = getSecretKey()
            val enc = JarvisCryptography.encrypt(targetPlainState, key)
            repository.insertIotDevice(device.copy(encryptedState = enc, lastUpdated = System.currentTimeMillis()))
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllVoiceCommands()
        }
    }
}
