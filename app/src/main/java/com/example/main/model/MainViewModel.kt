package com.example.main.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juul.kable.Advertisement
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json.Default.decodeFromString


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val snackbarHostState = SnackbarHostState()
    private val Context.dataStore by preferencesDataStore(name = "ui_state")
    private val dataStore = application.dataStore
    private val datastoreManager = DatastoreManager(dataStore)

    private val bleRepository = BleRepository()


    // Persistenter State

    private val _pState = MutableStateFlow(PersistantUiState())
    val pState: StateFlow<PersistantUiState> get() = _pState

    // non persistenter State

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> get() = _state


    init {
        // Hier können "Beobachter" auf Zustandsänderungen initialisiert werden
        // z.B. um den UI-Zustand zu speichern oder um auf Änderungen zu reagieren

        // Lade den persistenten UI-Zustand
        viewModelScope.launch {
            datastoreManager.getPersistantState().collectLatest { persistedState ->
                _pState.value = persistedState
            }
            Log.i(">>>>>", "loading Preferences: ${_pState.value}")
        }


        // Überwache den persistant state und speichere ihn bei Änderungen
        viewModelScope.launch {
            _pState.collectLatest {
                val pState = _pState.value
                datastoreManager.savePersitantState(pState)
            }
        }

        // Überwache den state und triggere Aktionen bei bestimmeten Zuständen
        // Hier als Beispiel: Wenn der Counter durch 5 teilbar ist, zeige eine Snackbar

    }


    // Actions
    // ------------------------------------------------------------------------------

    fun setReceiveData(receiveData: Boolean) {
        _state.value = _state.value.copy(receiveData = receiveData)
        if (receiveData) startReceivingData() else stopReceivingData()
    }

    fun setLed(led: Boolean) {
        _state.value = _state.value.copy(led = led)
        sendDataToDevice(Esp32DataOut(if (led) "H" else "L", _state.value.blink))
    }

    fun setBlink(blink: Boolean) {
        _state.value = _state.value.copy(blink = blink)
        sendDataToDevice(Esp32DataOut(if (_state.value.led) "H" else "L", blink))
    }


    // BLE Repository instance for scanning

    // Job reference for the scanning collection
    private var scanJob: Job? = null

    fun startDeviceScan() {
        scanJob = viewModelScope.launch {
            bleRepository.scanAdvertisements().collect { advertisement ->
                // Process the received advertisement (e.g., log it, update UI state, etc.)
                // Convert advertisement to peripheral

                val currentAds = _state.value.advertisements
                if (currentAds.none { it.identifier == advertisement.identifier }) {
                    _state.value = _state.value.copy(advertisements = currentAds + advertisement)
                }
                Log.i(">>>>>", "Scan ${state.value.advertisements}")

            }
        }
    }

    fun stopDeviceScan() {
        scanJob?.cancel()
        scanJob = null
    }

    fun setSelectedAdvertisement(advertisement: Advertisement) {
        _state.value = _state.value.copy(
            selectedAdvertisement = advertisement,
            connectionState = ConnectionState.NOT_CONNECTED
        )
    }


    // Verbindung aufbauen
    fun connect(advertisement: Advertisement?) {
        if (advertisement == null) return
        viewModelScope.launch {
            viewModelScope.launch {
                bleRepository.connectToPeripheral(advertisement)?.collectLatest { connectionState ->
                    Log.i(">>>>>", "Connection State: $connectionState")
                    _state.value = _state.value.copy(connectionState = connectionState)
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            bleRepository.disconnectFromPeripheral()
            _state.value = _state.value.copy(
                connectionState = ConnectionState.NOT_CONNECTED,
                receiveData = false
            )
        }
    }


    private var receiveDataJob: Job? = null

    fun startReceivingData() {
        receiveDataJob = viewModelScope.launch {
            bleRepository.startReceivingData()?.collectLatest { data ->
                // Process the received data (e.g., log it, update UI state, etc.)
                Log.i(">>>>>", "Received data: ${String(data, Charsets.UTF_8)}")
                val jsonString = String(data, Charsets.UTF_8)
                val esp32DataIn = parseEsp32Data(jsonString)
                _state.value = _state.value.copy(esp32DataIn = esp32DataIn)
            }

        }

    }

    fun stopReceivingData() {
        receiveDataJob?.cancel()
        receiveDataJob = null
    }

    fun sendDataToDevice(data: Esp32DataOut) {
        if (_state.value.connectionState != ConnectionState.CONNECTED) return

        viewModelScope.launch {
            bleRepository.sendData(data)
        }

    }

    // Ab hier Helper Funktionen
    // ------------------------------------------------------------------------------

    private fun parseEsp32Data(jsonString: String): Esp32DataIn? {
        return try {
            decodeFromString<Esp32DataIn>(jsonString)
        } catch (e: Exception) {
            null
        }
    }


    // Snackbar
    // ------------------------------------------------------------------------------

    fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        Log.i(">>>>>", "showSnackbar: $message")
        viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }


    // Zugriff auf String Ressourcen
    // ------------------------------------------------------------------------------
    private fun getStringRessource(resId: Int): String {
        return getApplication<Application>().getString(resId)
    }

    // beim Beenden des ViewModels
    // -----------------------------------------------------------------------------

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

}
