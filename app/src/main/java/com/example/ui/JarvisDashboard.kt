package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.IotDeviceEntity
import com.example.data.PluginEntity
import com.example.data.VoiceCommandEntity
import java.util.Date

// Clean Minimalism theme colors (Light Palette)
val PrimaryBlue = Color(0xFF005AC1)
val LightBlue = Color(0xFFD3E4FF)
val DeepBlue = Color(0xFF475AD7)
val LightBackground = Color(0xFFF7F9FC)
val CardBackgroundLight = Color(0xFFFFFFFF)
val BorderColor = Color(0xFFDEE3EB)
val DarkText = Color(0xFF001D35)
val NormalText = Color(0xFF1C1B1F)
val GrayText = Color(0xFF74777F)
val SecondaryText = Color(0xFF44474E)

// Keep original variables mapped to standard theme colors for compatibility
val NeonCyan = PrimaryBlue
val NeonBlue = DeepBlue
val DarkBackground = LightBackground
val CardBackground = CardBackgroundLight
val TerminalGreen = Color(0xFF2E7D32) // Soft forest green
val AccentRed = Color(0xFFC62828)     // Soft crimson red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarvisDashboard(viewModel: JarvisViewModel) {
    val isOffline by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val encryptionKeyPhrase by viewModel.encryptionKeyPhrase.collectAsStateWithLifecycle()
    val executionLog by viewModel.executionLog.collectAsStateWithLifecycle()
    val voiceTranscript by viewModel.voiceTranscript.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBackground,
                    titleContentColor = DarkText
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isOffline) TerminalGreen else AccentRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AXON Core",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.5).sp,
                                fontSize = 22.sp,
                                color = DarkText
                            )
                        }
                        
                        // Offline Protocol Mode Badge Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isOffline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                .border(1.dp, if (isOffline) Color(0xFFC8E6C9) else Color(0xFFFFE0B2), RoundedCornerShape(50))
                                .clickable { viewModel.isOfflineMode.value = !isOffline }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isOffline) Color(0xFF2E7D32) else Color(0xFFF57C00))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOffline) "SECURE OFFLINE NODE" else "CLOUD RETRIEVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = if (isOffline) Color(0xFF1B5E20) else Color(0xFFE65100)
                            )
                        }
                    }
                }
            )
        },
        containerColor = LightBackground,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2F3033),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Core HUD") },
                    label = { Text("Core HUD", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = PrimaryBlue,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Control Matrix") },
                    label = { Text("Device", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = PrimaryBlue,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.List, contentDescription = "IoT Deck") },
                    label = { Text("IoT Deck", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = PrimaryBlue,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Plugin Forge") },
                    label = { Text("Plugins", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = PrimaryBlue,
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightBackground)
        ) {
            // Main views based on activeTab
            when (activeTab) {
                0 -> HubDashboardView(viewModel)
                1 -> DeviceSecurityVaultView(viewModel)
                2 -> HomeAutomationView(viewModel)
                3 -> PluginForgeView(viewModel)
            }
        }
    }
}

// ------------------- VIEW 0: CORE HUD DASHBOARD -------------------
@Composable
fun HubDashboardView(viewModel: JarvisViewModel) {
    var queryInput by remember { mutableStateOf("") }
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val executionLog by viewModel.executionLog.collectAsStateWithLifecycle()
    val voiceCommands by viewModel.voiceCommands.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Pulsing Circle Canvas Core
        item {
            JarvisPulsingCoreCard(isListening, onMicrophoneClicked = {
                viewModel.isListening.value = !isListening
                if (viewModel.isListening.value) {
                    viewModel.executionLog.value = "Hardware sound waves analysis activated locally..."
                    // Automatically choose a random voice prompt for simulation after a delay
                    val prompts = listOf(
                        "Jarvis, volume to 85%",
                        "Jarvis, raise tablet volume",
                        "Jarvis, dim living room lights to 20%",
                        "Jarvis, secure perimeter gate",
                        "Jarvis, temperature set core to 21 degrees",
                        "Jarvis, state current battery level",
                        "Jarvis, run neural scanner logs"
                    )
                    viewModel.voiceTranscript.value = prompts.random()
                    viewModel.submitQuery(viewModel.voiceTranscript.value)
                    viewModel.isListening.value = false
                }
            })
        }

        // Live Telemetry Indicators Header
        item {
            Text(
                text = "REAL-TIME DIAGNOSTIC TELEMETRY",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            TelemetryGridPanel(viewModel)
        }

        // Action Command Log Terminal
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMMAND DISPATCH ANALYZER",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TerminalGreen
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(TerminalGreen)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = executionLog,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = DarkText,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth().testTag("dispatch_output_terminal")
                    )
                }
            }
        }

        // Input Console Box
        item {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = queryInput,
                onValueChange = { queryInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("command_input_field"),
                placeholder = {
                    Text(
                        "Input system manual override query...",
                        color = GrayText,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (queryInput.isNotEmpty()) {
                                viewModel.submitQuery(queryInput)
                                queryInput = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier.testTag("send_command_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Submit", tint = PrimaryBlue)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = DarkText,
                    unfocusedTextColor = NormalText,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    if (queryInput.isNotEmpty()) {
                        viewModel.submitQuery(queryInput)
                        queryInput = ""
                        focusManager.clearFocus()
                    }
                })
            )
        }

        // Preloaded vocal commands list
        item {
            Text(
                text = "SUGGESTED SECURE TRIGGERS (TAP TO DISPATCH)",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GrayText,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = listOf("volume to 80%", "dim living light", "secure perimeter gate")
                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.selectQuickVoiceAction(preset)
                            }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.uppercase(),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ------------------- VIEW 1: DEVICE SECURITY VAULT / CONTROLS -------------------
@Composable
fun DeviceSecurityVaultView(viewModel: JarvisViewModel) {
    val volume by viewModel.deviceVolume.collectAsStateWithLifecycle()
    val brightness by viewModel.deviceBrightness.collectAsStateWithLifecycle()
    val isWifiOn by viewModel.isWifiOn.collectAsStateWithLifecycle()
    val isBluetoothOn by viewModel.isBluetoothOn.collectAsStateWithLifecycle()
    val encryptionKeyPhrase by viewModel.encryptionKeyPhrase.collectAsStateWithLifecycle()
    val voiceCommands by viewModel.voiceCommands.collectAsStateWithLifecycle()
    val selectedLogId by viewModel.selectedEncryptedLogId.collectAsStateWithLifecycle()
    val decryptedText by viewModel.decryptedLogText.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Physical mobile hardware overrides
        item {
            Text(
                text = "LOCAL HARDWARE INTERFACE",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = PrimaryBlue,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Volume Control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Vol", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("System Volume", color = DarkText, fontSize = 13.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
                        }
                        Text("${volume}%", color = PrimaryBlue, fontSize = 12.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = volume.toFloat(),
                        onValueChange = { viewModel.deviceVolume.value = it.toInt() },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = BorderColor
                        ),
                        modifier = Modifier.testTag("volume_slider_override")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Brightness Control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Bright", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Screen Brightness", color = DarkText, fontSize = 13.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
                        }
                        Text("${brightness}%", color = PrimaryBlue, fontSize = 12.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = brightness.toFloat(),
                        onValueChange = { viewModel.deviceBrightness.value = it.toInt() },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = BorderColor
                        ),
                        modifier = Modifier.testTag("brightness_slider_override")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Wireless Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Wi-Fi State", color = NormalText, fontSize = 12.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isWifiOn,
                                onCheckedChange = { viewModel.isWifiOn.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryBlue,
                                    uncheckedThumbColor = GrayText,
                                    uncheckedTrackColor = BorderColor
                                )
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Bluetooth BT", color = NormalText, fontSize = 12.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isBluetoothOn,
                                onCheckedChange = { viewModel.isBluetoothOn.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryBlue,
                                    uncheckedThumbColor = GrayText,
                                    uncheckedTrackColor = BorderColor
                                )
                            )
                        }
                    }
                }
            }
        }

        // Configuration E2E Key Settings
        item {
            Text(
                text = "END-TO-END CRYPTOGRAPHIC MATRIX",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize physical passphrase key to alter local E2E database encryption standard:",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = encryptionKeyPhrase,
                        onValueChange = { viewModel.encryptionKeyPhrase.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = DarkText,
                            unfocusedTextColor = NormalText,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Encryption Standard: AES-256-CBC\nKey Derivation: SHA-256 PBKDF-hash\nHardware Triggers: Secure Local Matrix (Isolated from API)",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = PrimaryBlue.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Database Logs with direct Cipher visualizer
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOCAL PERSISTED ENCRYPTED SQL DATA",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GrayText
                )
                Text(
                    text = "CLEAR ALL",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = AccentRed,
                    modifier = Modifier.clickable {
                        viewModel.clearAllLogs()
                    }
                )
            }
        }

        if (voiceCommands.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No database logs recorded yet.",
                        color = GrayText,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(voiceCommands) { cmd ->
                val isSelected = selectedLogId == cmd.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isSelected) PrimaryBlue else BorderColor,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.selectLogForDecryption(cmd) },
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "LOG-ID: ${cmd.id.take(8).uppercase()}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cmd.isSuccess) TerminalGreen else AccentRed
                            )
                            Text(
                                text = if (cmd.isOffline) "OFFLINE SOURCE" else "CLOUD RETRIEVE",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp,
                                color = if (cmd.isOffline) TerminalGreen else PrimaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Cipher text request:",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue.copy(alpha = 0.7f)
                        )
                        Text(
                            text = cmd.encryptedText.take(35) + "...",
                            fontSize = 11.sp,
                            maxLines = 1,
                            fontFamily = FontFamily.Monospace,
                            color = SecondaryText
                        )

                        if (isSelected && decryptedText != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0F4FA))
                                    .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "[DECRYPTED VIA STATION KEY]",
                                            color = TerminalGreen,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = AccentRed,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { viewModel.closeDecryptionVisualizer() }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = decryptedText!!,
                                        color = DarkText,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- VIEW 2: SMART HOME AUTOMATION -------------------
@Composable
fun HomeAutomationView(viewModel: JarvisViewModel) {
    val items by viewModel.iotDevices.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "HOME AUTOMATION ACCESS GATE",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = PrimaryBlue,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Secure local hardware relays, cryptographically signed and stored in local memory.",
                fontSize = 11.sp,
                color = GrayText,
                fontFamily = FontFamily.SansSerif
            )
        }

        items(items) { device ->
            // Try to decrypt the state locally
            val plainState = viewModel.decryptIotState(device.encryptedState)
            
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = device.name.uppercase(),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "CATEGORY: ${device.category}",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = plainState,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Encrypted State Envelope Store: ${device.encryptedState.take(30)}...",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Specific command toggle triggers depending on categories
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (device.category) {
                            "LIGHT" -> {
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "LIGHT_ON:100%") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("LIGHT FULL 100%", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "LIGHT_OFF") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("TURNOFF LIGHT", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = AccentRed)
                                }
                            }
                            "LOCK" -> {
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "VAL_OPEN") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("OPEN DOORS", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                }
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "VAL_LOCKED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("LOCK AND SECURE", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = TerminalGreen)
                                }
                            }
                            "TEMPERATURE" -> {
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "TEMP:18.0C") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("AC FULL COOL (18C)", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "TEMP:24.0C") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("AC TEMP SYNC (24C)", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = DarkText)
                                }
                            }
                            "ALARM" -> {
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "ARMED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("ARM SHIELDS", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = AccentRed)
                                }
                                Button(
                                    onClick = { viewModel.triggerSmartTriggerDirectly(device, "DISARMED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("DISARM ALARM", fontSize = 10.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = DarkText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- VIEW 3: THIRD-PARTY PLUGIN FORGE -------------------
@Composable
fun PluginForgeView(viewModel: JarvisViewModel) {
    val pluginsList by viewModel.plugins.collectAsStateWithLifecycle()

    var showForgeForm by remember { mutableStateOf(false) }
    var pluginNameInput by remember { mutableStateOf("") }
    var pluginDescInput by remember { mutableStateOf("") }
    var pluginScriptInput by remember { mutableStateOf("") }
    var pluginPermsInput by remember { mutableStateOf("SYSTEM_ACCESS,LOCAL_SECURE") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MODULAR COMPANION PLUGINS",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = PrimaryBlue
                )
                Button(
                    onClick = { showForgeForm = !showForgeForm },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (showForgeForm) "CLOSE FORGE" else "+ SECURE FORGE",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = PrimaryBlue
                    )
                }
            }
        }

        if (showForgeForm) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SECURE PLUGIN FORGE STRUCT",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = pluginNameInput,
                            onValueChange = { pluginNameInput = it },
                            label = { Text("Plugin Name", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("add_plugin_name_field"),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.SansSerif),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = DarkText,
                                unfocusedTextColor = NormalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pluginDescInput,
                            onValueChange = { pluginDescInput = it },
                            label = { Text("Description Capabilities", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = DarkText,
                                unfocusedTextColor = NormalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pluginPermsInput,
                            onValueChange = { pluginPermsInput = it },
                            label = { Text("Permissions (csv)", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.SansSerif),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = DarkText,
                                unfocusedTextColor = NormalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pluginScriptInput,
                            onValueChange = { pluginScriptInput = it },
                            label = { Text("Sandbox Actions Rule Engine Script", fontFamily = FontFamily.SansSerif, fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = DarkText,
                                unfocusedTextColor = NormalText,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                if (pluginNameInput.isNotEmpty()) {
                                    viewModel.registerNewCustomPlugin(
                                        name = pluginNameInput,
                                        desc = pluginDescInput,
                                        script = pluginScriptInput,
                                        perms = pluginPermsInput
                                    )
                                    pluginNameInput = ""
                                    pluginDescInput = ""
                                    pluginScriptInput = ""
                                    showForgeForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier.fillMaxWidth().testTag("submit_plugin_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("BUILD & LOAD SECURE WRAPPER", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        items(pluginsList) { plugin ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (plugin.isEnabled) PrimaryBlue.copy(alpha = 0.5f) else BorderColor,
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = plugin.name,
                                    color = DarkText,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "v${plugin.version}",
                                    color = PrimaryBlue,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "ID: ${plugin.pluginId}",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = GrayText
                            )
                        }
                        
                        Switch(
                            checked = plugin.isEnabled,
                            onCheckedChange = { viewModel.togglePlugin(plugin) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue,
                                uncheckedThumbColor = GrayText,
                                uncheckedTrackColor = BorderColor
                            )
                        )
                    }
                    
                    Text(
                        text = plugin.description,
                        fontSize = 12.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "PERMISSIONS ASSIGNMENTS: ${plugin.permissionsRequested}",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LOCALLY ENCRYPTED TRIGGER SNIPPET: \n${plugin.scriptSnippet}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = GrayText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F4FA))
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                    
                    if (plugin.pluginId.startsWith("p_custom_")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DELETE PLUGIN",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = AccentRed,
                            modifier = Modifier
                                .clickable { viewModel.deletePlugin(plugin) }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}


// --- DYNAMIC PULSING REACTOR CORE ANIMATION COMPONENT ---
@Composable
fun JarvisPulsingCoreCard(isListening: Boolean, onMicrophoneClicked: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val coreRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(28.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(190.dp)
                    .clickable { onMicrophoneClicked() }
            ) {
                // Pulse 1: Outer glowing ring
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(50))
                        .border(
                            width = (3.dp * pulseScale),
                            color = PrimaryBlue.copy(alpha = 0.05f / pulseScale),
                            shape = RoundedCornerShape(50)
                        )
                )

                // Pulse 2: Middle ring
                Box(
                    modifier = Modifier
                        .size(148.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.12f), RoundedCornerShape(50))
                )

                // Main Central white capsule button with inner blue-indigo gradient
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(50))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(98.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, DeepBlue)
                                )
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Warning else Icons.Default.Face,
                                contentDescription = "Engine Core Trigger",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp).rotate(if (isListening) coreRotation else 0f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isListening) "ACTIVE" else "READY",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isListening) "LOCAL SESSION ACTIVE..." else "SECURE NODE IDLE",
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText,
                letterSpacing = (-0.2).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isListening) "Analyzing hardware local voice waveforms..." else "Encrypted local session active. Listening for \"Hey Axon\"",
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = GrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ------------------- COMPONENT: TELEMETRY METRICS -------------------
@Composable
fun TelemetryGridPanel(viewModel: JarvisViewModel) {
    val level by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val utilization by viewModel.cpuUtilization.collectAsStateWithLifecycle()
    val temp by viewModel.tempSensor.collectAsStateWithLifecycle()
    val ram by viewModel.ramState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // CPU utilization Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.weight(1f).border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("LOCAL CPU", fontSize = 9.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = GrayText)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${utilization}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif, color = DarkText)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { utilization / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = PrimaryBlue,
                    trackColor = BorderColor
                )
            }
        }

        // Battery level Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.weight(1f).border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("BATTERY CELLS", fontSize = 9.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = GrayText)
                Spacer(modifier = Modifier.height(2.dp))
                Text("${level}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif, color = DarkText)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { level / 100f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = PrimaryBlue,
                    trackColor = BorderColor
                )
            }
        }

        // Temperature Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            modifier = Modifier.weight(1f).border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("TEMPERATURE", fontSize = 9.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = GrayText)
                Spacer(modifier = Modifier.height(2.dp))
                Text(String.format("%.1f°C", temp), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif, color = DarkText)
                Spacer(modifier = Modifier.height(6.dp))
                Text("THERM SAFE", fontSize = 8.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = TerminalGreen)
            }
         }
    }
}

