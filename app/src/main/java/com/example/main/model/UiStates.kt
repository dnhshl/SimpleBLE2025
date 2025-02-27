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


// Persistenter UI-Zustand
@Serializable
data class PersistantUiState(
    val name: String = ""
)

// Nicht persistenter UI-Zustand
data class UiState(
    val advertisements: List<Advertisement> = emptyList(),
    val selectedAdvertisement: Advertisement? = null,
    val connectionState: ConnectionState = ConnectionState.NO_DEVICE,
    val receiveData: Boolean = false,
    val led: Boolean = false,
    val blink: Boolean = false,
    val esp32DataIn: Esp32DataIn? = null
)



