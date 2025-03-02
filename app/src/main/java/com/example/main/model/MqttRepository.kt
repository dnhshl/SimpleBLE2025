package com.example.main.model


import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import java.io.File
import kotlin.text.Charsets.UTF_8


const val MQTT_BROKER = "tcp://broker.mqtt.cool:1883"
const val MQTT_MAIN_TOPIC= "59z83bq"

class MqttRepository(private val context: Context) {

    private val client_id = java.util.UUID.randomUUID().toString()
    private var topicIn = ""
    private var topicOut = ""
    private var mqttClient: MqttClient? = null

    private val _mqttState = MutableStateFlow<ConnectionState>(ConnectionState.NOT_CONNECTED)
    val mqttState: Flow<ConnectionState> get() = _mqttState

    // Flow der eingehenden Nachrichten
    private val _incomingMessages = MutableStateFlow<Esp32DataIn?>(null)
    val incomingMessages: Flow<Esp32DataIn?> get() = _incomingMessages

    // Funktion, die aufgerufen wird, wenn eine Nachricht eintrifft
    // und die Nachricht dekodiert und in den Flow _incomingMessages schreibt
    private fun onMessageArrive(topic: String?, message: MqttMessage?) {
        message?.payload?.let {
            val jsonString = String(it, UTF_8)
            try {
                val data = decodeFromString<Esp32DataIn>(jsonString)
                _incomingMessages.value = data
            } catch (e: Exception) {
                _incomingMessages.value = null
                Log.e("MqttRepository", "Failed to decode message", e)
            }
        }
    }

    // Flow über definierte Schnittstelle der App bereitstellen
    fun receiveData(): Flow<Esp32DataIn?> {
        return incomingMessages
    }


    fun collectDeviceInfo(): Flow<Device> {
        val espDevices = listOf(
            Device(title = "Anjas ESP", subtitle = "da geht was", id = "esp32-1-abc39xy"),
            Device(title = "Willis ESP", subtitle = "auch schön", id = "esp32-1-cde45fg"),
            Device(title = "Franzis ESP", subtitle = "warum nicht", id = "esp32-1-xyz12ab"),
        )
        return espDevices.asFlow()
    }


    fun connect(device: Device): Flow<ConnectionState> {
        try {
            // Zugriff auf internen Speicher zur Ablage von Verwaltungsinfos
            val persistenceDir = File(context.filesDir, "mqtt-persistence")
            if (!persistenceDir.exists()) {
                if (!persistenceDir.mkdirs()) {
                    throw Exception("Failed to create persistence directory")
                }
            }
            val persistence = MqttDefaultFilePersistence(persistenceDir.absolutePath)

            // Topics für die Kommunikation festlegen
            val esp32ClientId = device.id
            topicOut = "$MQTT_MAIN_TOPIC/$esp32ClientId/config"
            topicIn = "$MQTT_MAIN_TOPIC/$esp32ClientId/data"

            // MQTT-Client initialisieren
            mqttClient = MqttClient(MQTT_BROKER, client_id, persistence)

            // Callback für eingehende Nachrichten setzen
            mqttClient?.setCallback(object : MqttCallback {
                // Was passiert, wenn die Verbindung verloren geht?
                override fun connectionLost(cause: Throwable?) {
                    _mqttState.value = ConnectionState.NOT_CONNECTED
                }

                // Was passiert, wenn eine Nachricht eintrifft?
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    onMessageArrive(topic, message)
                }

                // Was passiert, wenn eine Nachricht ausgeliefert wurde?
                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })

            // verbinden
            mqttClient?.connect()
            // topic subscriben
            mqttClient?.subscribe(topicIn)
            // Verbindungsstatus aktualisieren
            _mqttState.value = ConnectionState.CONNECTED
        } catch (e: MqttException) {
            // Handle MQTT-specific exceptions
            Log.e("MQTT", "MQTT Exception: ${e.message}", e)
        } catch (e: Exception) {
            // Handle other exceptions (e.g., IOException)
            Log.e("MQTT", "General Exception: ${e.message}", e)
        }
        return mqttState
    }

    fun disconnect() {
        mqttClient?.disconnect()
        _mqttState.value = ConnectionState.NOT_CONNECTED
    }

    fun sendData(data: Esp32DataOut) {
        val jsonString = Json.encodeToString(data)
        val message = MqttMessage(jsonString.toByteArray(UTF_8))
        mqttClient?.publish(topicOut, message)
    }
}