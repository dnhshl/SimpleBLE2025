package com.example.main.model

import android.util.Log
import com.juul.kable.Advertisement
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * Repository class for handling BLE operations.
 */
@OptIn(ExperimentalUuidApi::class)
class BleRepository {

    private val customServiceUuid = Uuid.parse("0000FFE0-0000-1000-8000-00805F9B34FB")
    private val customCharacteristicUuid = Uuid.parse("0000FFE1-0000-1000-8000-00805F9B34FB")

    private val customEsp32Characteristic = characteristicOf(
        customServiceUuid,
        customCharacteristicUuid
    )

    private var peripheral: Peripheral? = null


    /**
     * Function to obtain a Flow of advertisements.
     * Scanning starts when the Flow is collected and stops when collection is terminated.
     *
     * @return Flow of Advertisement objects.
     */
    fun scanAdvertisements(): Flow<Advertisement> {
        return Scanner {
            filters {
                match {
                    services = listOf(customServiceUuid)
                }
            }
        }.advertisements
    }

    /**
     * Connects to a peripheral using the provided advertisement.
     *
     * @param advertisement The Advertisement object to connect to.
     * @return Flow of ConnectionState representing the connection state.
     */
    suspend fun connectToPeripheral(advertisement: Advertisement): Flow<ConnectionState>? {
        peripheral = Peripheral(advertisement) {
            onServicesDiscovered {
                requestMtu(512)
            }
        }
        peripheral?.connect()
        return peripheral?.state?.map { connectionState ->
            when (connectionState) {
                is State.Connected -> ConnectionState.CONNECTED
                is State.Disconnected -> ConnectionState.NOT_CONNECTED
                is State.Connecting -> ConnectionState.CONNECTING
                else -> ConnectionState.NO_DEVICE
            }
        }
    }

    /**
     * Disconnects from the currently connected peripheral.
     */
    suspend fun disconnectFromPeripheral() {
        peripheral?.disconnect()
        delay(500) // Add a delay to ensure the peripheral processes the disconnect
        peripheral = null
    }

    /**
     * Starts receiving data from the connected peripheral.
     *
     * @return Flow of ByteArray representing the received data.
     */
    fun receiveData(): Flow<Esp32DataIn?>? {
        return peripheral?.observe(customEsp32Characteristic)?.map { data ->
            val jsonString = String(data, Charsets.UTF_8)
            try {
                decodeFromString<Esp32DataIn>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
    }


    /**
     * Sends data to the connected peripheral.
     *
     * @param data The Esp32DataOut object to send.
     */
    suspend fun sendData(data: Esp32DataOut) {
        val jsonString = Json.encodeToString(data)
        val dataAsByteArray = jsonString.toByteArray(Charsets.UTF_8)
        Log.i(">>>>>", "Sending data: $data, $jsonString, $dataAsByteArray")
        peripheral?.write(
            characteristic = customEsp32Characteristic,
            data = dataAsByteArray,
            writeType = WriteType.WithResponse
        )
    }
}