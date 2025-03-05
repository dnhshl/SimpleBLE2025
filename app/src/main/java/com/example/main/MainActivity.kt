package com.example.main

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.main.model.NfcRepository
import com.example.main.ui.screens.MyApp
import com.example.main.ui.theme.MultiScreenNavTemplateTheme
import com.github.skjolber.ndef.Message
import com.github.skjolber.ndef.Record
import com.github.skjolber.ndef.externaltype.AndroidApplicationRecord
import com.github.skjolber.ndef.wellknown.TextRecord


class MainActivity : ComponentActivity() {

    private val nfcAdapter: NfcAdapter by lazy { NfcAdapter.getDefaultAdapter(applicationContext) }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        NfcRepository.processNfcIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //val intent = intent
        Log.i(">>>>", "onCreate: ${intent.action}")
        NfcRepository.processNfcIntent(intent)

        setContent {
            MultiScreenNavTemplateTheme {
                MyApp()
            }
        }
    }



    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e(">>>>", "Error enabling NFC foreground dispatch", ex)
        }
    }


    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(">>>>", "Error disabling NFC foreground dispatch", ex)
        }
    }

}
