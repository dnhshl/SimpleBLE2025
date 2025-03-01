package com.example.main.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val snackbarHostState = SnackbarHostState()
    private val Context.dataStore by preferencesDataStore(name = "ui_state")
    private val dataStore = application.dataStore
    private val datastoreManager = DatastoreManager(dataStore)

    private val communicationRepository = MqttRepository(application)


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

    fun startCollectDevices() {
        scanJob = viewModelScope.launch {
            communicationRepository.collectDeviceInfo().collect { device ->
                // Process the received advertisement (e.g., log it, update UI state, etc.)
                // Convert advertisement to peripheral

                val currentDevices = _state.value.devices
                if (currentDevices.none { it.id == device.id }) {
                    _state.value = _state.value.copy(devices = currentDevices + device)
                }
                Log.i(">>>>>", "Scan ${state.value.devices}")
            }
        }
    }

    fun stopCollectDevices() {
        scanJob?.cancel()
        scanJob = null
    }

    fun setSelectedDevice(device: Device) {
        _state.value = _state.value.copy(
            selectedDevice = device,
            connectionState = ConnectionState.NOT_CONNECTED
        )
    }


    // Verbindung aufbauen
    fun connect(device: Device?) {
        device ?: return
        viewModelScope.launch {
            viewModelScope.launch {
                communicationRepository.connect(device)?.collectLatest { connectionState ->
                    Log.i(">>>>>", "Connection State: $connectionState")
                    _state.value = _state.value.copy(connectionState = connectionState)
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            communicationRepository.disconnect()
            _state.value = _state.value.copy(
                connectionState = ConnectionState.NOT_CONNECTED,
                receiveData = false
            )
        }
    }


    private var receiveDataJob: Job? = null

    fun startReceivingData() {
        receiveDataJob = viewModelScope.launch {
            communicationRepository.receiveData()?.collectLatest { data ->
                _state.value = _state.value.copy(esp32DataIn = data)
            }
        }
    }

    fun stopReceivingData() {
        receiveDataJob?.cancel()
        receiveDataJob = null
    }

    private fun sendDataToDevice(data: Esp32DataOut) {
        if (_state.value.connectionState != ConnectionState.CONNECTED) return

        viewModelScope.launch {
            communicationRepository.sendData(data)
        }
    }

    // Ab hier Helper Funktionen
    // ------------------------------------------------------------------------------



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
