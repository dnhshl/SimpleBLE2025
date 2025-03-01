package com.example.main.model


import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence

import kotlin.text.Charsets.UTF_8

class MqttRepository {

    private val brokerUrl = "tcp://your-broker-url:1883"
    private val clientId = java.util.UUID.randomUUID().toString()
    private val topic = "your/topic"
    private var mqttClient: MqttClient? = null

    private val _mqttState = MutableStateFlow<ConnectionState>(ConnectionState.NO_DEVICE)
    val mqttState: Flow<ConnectionState> get() = _mqttState

    init {
        connect()
    }

    private fun connect() {
        mqttClient = MqttClient(brokerUrl, clientId, MqttDefaultFilePersistence())
        mqttClient?.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                _mqttState.value = ConnectionState.NOT_CONNECTED
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                message?.payload?.let {
                    val jsonString = String(it, UTF_8)
                    try {
                        val data = decodeFromString<Esp32DataIn>(jsonString)
                        // Handle received data
                    } catch (e: Exception) {
                        Log.e("MqttRepository", "Failed to decode message", e)
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })
        mqttClient?.connect()
        _mqttState.value = ConnectionState.CONNECTED
    }

    fun disconnect() {
        mqttClient?.disconnect()
        _mqttState.value = ConnectionState.NOT_CONNECTED
    }

    fun sendData(data: Esp32DataOut) {
        val jsonString = Json.encodeToString(data)
        val message = MqttMessage(jsonString.toByteArray(UTF_8))
        mqttClient?.publish(topic, message)
    }

    fun receiveData(): Flow<Esp32DataIn?> {
        return _mqttState.map { connectionState ->
            if (connectionState == ConnectionState.CONNECTED) {
                // Implement logic to handle received data
                null
            } else {
                null
            }
        }
    }
}