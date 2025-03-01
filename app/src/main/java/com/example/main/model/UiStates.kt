package com.example.main.model

import com.juul.kable.Advertisement
import kotlinx.serialization.Serializable


// Datenklassen für den UI-Zustand
// ----------------------------------------------------------------


enum class  ConnectionState {
    NO_DEVICE,
    NOT_CONNECTED,
    CONNECTING,
    CONNECTED
}

data class Device(
    val title: String,                      // Name des Geräts zur Anzeige
    val subtitle: String,                   // Zusätzliche Informationen zur Anzeige
    val id: String,                         // eindeutige id des Geräts
    val advertisement: Advertisement        // Systeminformationen des Geräts
)

// Persistenter UI-Zustand
@Serializable
data class PersistantUiState(
    val name: String = ""
)

// Nicht persistenter UI-Zustand
data class UiState(
    val devices: List<Device> = emptyList(),
    val selectedDevice: Device? = null,
    val connectionState: ConnectionState = ConnectionState.NO_DEVICE,
    val receiveData: Boolean = false,
    val led: Boolean = false,
    val blink: Boolean = false,
    val esp32DataIn: Esp32DataIn? = null
)



