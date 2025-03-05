package com.example.main.model

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import com.example.main.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.github.skjolber.ndef.Message
import com.github.skjolber.ndef.Record
import com.github.skjolber.ndef.externaltype.AndroidApplicationRecord
import com.github.skjolber.ndef.wellknown.TextRecord


object NfcRepository {

    private val _nfcData = MutableStateFlow<String?>(null)
    val nfcData: StateFlow<String?> get() = _nfcData

    fun processNfcIntent(intent: Intent) {

        if (!intent.hasExtra(NfcAdapter.EXTRA_TAG)) return

        val messages =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, Tag::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            }

        val message = messages?.get(0) as NdefMessage
        val records: List<Record> = Message(message)
        for (record in records) {
            when (record) {
                // Wir reagieren nur auf TextRecords
                is TextRecord -> {
                    Log.i(">>>>>", "TextRecord: ${record.text}")
                    _nfcData.value = null        // Reset
                    _nfcData.value = record.text
                }
                // Ausgabe des AAR Records nur zu Info im Log
                is AndroidApplicationRecord -> Log.i(">>>", "AAR is ${record.packageName}")
            }
        }
    }
}