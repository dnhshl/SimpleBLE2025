package com.example.main.model


import android.util.Log
import com.juul.kable.Advertisement
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.WriteType
import com.juul.kable.characteristicOf
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Esp32DataIn(
    val potiArray: List<Int>,
    val ledstatus: String
)

@Serializable
data class Esp32DataOut(
    val LED: String,            // "H" or "L"
    val LEDBlinken: Boolean
)


@OptIn(ExperimentalUuidApi::class)
class BleRepository {


    private val customServiceUuid = Uuid.parse("0000FFE0-0000-1000-8000-00805F9B34FB")
    private val customCharacteristicUuid = Uuid.parse("0000FFE1-0000-1000-8000-00805F9B34FB")


    private val customEsp32Characteristic = characteristicOf(
        customServiceUuid,
        customCharacteristicUuid
    )


    // Function to obtain a Flow of advertisements.
    // Scanning starts when the Flow is collected and stops when collection is terminated.

    fun scanAdvertisements(): Flow<Advertisement> {
        return Scanner {
            filters {
                //match {
                //    services = listOf(customServiceUuid)
                //}
            }
        }.advertisements
    }

    suspend fun connectToPeripheral(advertisement: Advertisement): Peripheral {
        val peripheral = Peripheral(advertisement) {
            onServicesDiscovered {
                requestMtu(512)
            }
        }
        peripheral.connect()
        return peripheral
    }

    fun startReceivingData(peripheral: Peripheral): Flow<ByteArray> {
        return peripheral.observe(customEsp32Characteristic)
    }

    suspend fun sendData(peripheral: Peripheral, data: Esp32DataOut) {
        val jsonString = Json.encodeToString(data)
        val dataAsByteArray = jsonString.toByteArray(Charsets.UTF_8)
        Log.i(">>>>>", "Sending data: $data, $jsonString, $dataAsByteArray")
        peripheral.write(
            characteristic = customEsp32Characteristic,
            data = dataAsByteArray,
            writeType = WriteType.WithResponse
        )
    }

}

