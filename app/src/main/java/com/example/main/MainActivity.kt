package com.example.main

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.main.model.NfcRepository
import com.example.main.ui.screens.MyApp
import com.example.main.ui.theme.MultiScreenNavTemplateTheme


class MainActivity : ComponentActivity() {

    private val nfcAdapter: NfcAdapter by lazy { NfcAdapter.getDefaultAdapter(applicationContext) }

    // konfiguriert den NFC Adapter für den Empfang von NFC Intents in der App,
    // wenn die App im Vorderungd ist
    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e(">>>>", "Error enabling NFC foreground dispatch", ex)
        }
    }

    // deaktiviert den NFC Adapter für den Empfang von NFC Intents, wenn die App im Hintergrund ist
    // es greifen die Standard-Android Einstellungen
    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(">>>>", "Error disabling NFC foreground dispatch", ex)
        }
    }

    // wird aufgerufen, wenn ein neuer NFC Intent empfangen wird
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //die eigentliche Verarbeitung des NFC Intents wird in der NfcRepository Klasse durchgeführt
        NfcRepository.processNfcIntent(intent)
    }

    // aktiviert den NFC Adapter, wenn die App im Vordergrund ist
    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    // deaktiviert den NFC Adapter, wenn die App im Hintergrund ist
    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // verarbeiten von NFC Intents, die beim Start der App empfangen werden
        NfcRepository.processNfcIntent(intent)

        setContent {
            MultiScreenNavTemplateTheme {
                MyApp()
            }
        }
    }
}
