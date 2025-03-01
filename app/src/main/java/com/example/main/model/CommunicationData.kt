package com.example.main.model

import kotlinx.serialization.Serializable

/**
 * Data class representing the input data from ESP32.
 *
 * @property potiArray List of potentiometer values.
 * @property ledstatus Status of the LED.
 */
@Serializable
data class Esp32DataIn(
    val potiArray: List<Int>,
    val ledstatus: String
)

/**
 * Data class representing the output data to ESP32.
 *
 * @property LED Status of the LED ("H" or "L").
 * @property LEDBlinken Boolean indicating if the LED should blink.
 */
@Serializable
data class Esp32DataOut(
    val LED: String,
    val LEDBlinken: Boolean
)

const val MQTT_BROKER = "broker.mqtt.cool:1883"
val MQTT_CLIENT_ID = java.util.UUID.randomUUID().toString()
const val MQTT_MAIN_TOPIC= "59z83bq"