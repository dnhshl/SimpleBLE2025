package com.example.main.model

import com.juul.kable.Advertisement
import kotlinx.serialization.Serializable


// Datenklassen für den UI-Zustand
// ----------------------------------------------------------------


object ConnectionState {
    const val NO_DEVICE = 0
    const val NOT_CONNECTED = 1
    const val CONNECTING = 2
    const val CONNECTED = 3
}


// Persistenter UI-Zustand
@Serializable
data class PersistantUiState(
    val name: String = ""
)

// Nicht persistenter UI-Zustand
data class UiState(
    val clickCounter: Int = 0,
    val advertisements: List<Advertisement> = emptyList(),
    val selectedAdvertisement: Advertisement? = null,
    val connectionState: Int = ConnectionState.NO_DEVICE,
    val receiveData: Boolean = false,
    val led: Boolean = false,
    val blink: Boolean = false,
    val esp32DataIn: Esp32DataIn? = null
)



