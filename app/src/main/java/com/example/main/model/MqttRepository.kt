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

class MqttRepository(private val context: Context) {

    private val brokerUrl = "tcp://$MQTT_BROKER"
    private val clientId = MQTT_CLIENT_ID
    private var topicIn = ""
    private var topicOut = ""
    private var mqttClient: MqttClient? = null
    private var esp32ClientId = ""

    private val _mqttState = MutableStateFlow<ConnectionState>(ConnectionState.NOT_CONNECTED)
    val mqttState: Flow<ConnectionState> get() = _mqttState

    private val _incomingMessages = MutableStateFlow<Esp32DataIn?>(null)
    val incomingMessages: Flow<Esp32DataIn?> get() = _incomingMessages


    fun collectDeviceInfo(): Flow<Device> {
        val espDevices = listOf("esp32-1", "esp32-2", "esp32-3")
        return espDevices.map { Device(title = it, subtitle = "", id = it) }.asFlow()
    }


    fun connect(device: Device): Flow<ConnectionState> {
        try {
            val persistenceDir = File(context.filesDir, "mqtt-persistence")
            if (!persistenceDir.exists()) {
                if (!persistenceDir.mkdirs()) {
                    throw Exception("Failed to create persistence directory")
                }
            }
            val persistence = MqttDefaultFilePersistence(persistenceDir.absolutePath)

            esp32ClientId = device.id
            topicOut = "$MQTT_MAIN_TOPIC/$esp32ClientId/config"
            topicIn = "$MQTT_MAIN_TOPIC/$esp32ClientId/data"

            mqttClient = MqttClient(brokerUrl, clientId, persistence)
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    _mqttState.value = ConnectionState.NOT_CONNECTED
                }

                override fun messageArrived(topicIn: String?, message: MqttMessage?) {
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

                override fun deliveryComplete(token: IMqttDeliveryToken?) {}
            })
            val options = MqttConnectOptions()
            mqttClient?.connect(options)
            mqttClient?.subscribe(topicIn)
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

    fun receiveData(): Flow<Esp32DataIn?> {
        return incomingMessages
    }
}