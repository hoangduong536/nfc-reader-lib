package com.example.nfcreaderlib

import android.app.*
import android.content.*
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.*
import androidx.core.app.NotificationCompat


internal class NfcReaderService : Service() {

    companion object {
        var callback: NfcCallback? = null
    }

    override fun onCreate() {
        super.onCreate()
        createForegroundNotification()
    }

    private fun createForegroundNotification() {
        val channelId = "nfc_reader_service"
        val channelName = "NFC Reader Background Service"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("NFC đang chạy")
            .setContentText("Đang chờ quét thẻ NFC...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            processTag(intent)
        }
        return START_STICKY
    }

    private fun processTag(intent: Intent) {
        callback?.onProcessing()

        try {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndef = Ndef.get(tag)
            ndef?.connect()
            val message = ndef?.ndefMessage
            val payload = message?.records?.firstOrNull()?.payload
            val text = payload?.decodeToString() ?: "Không đọc được dữ liệu"
            callback?.onSuccess(text)
            ndef?.close()
        } catch (e: Exception) {
            callback?.onFailure("Lỗi đọc thẻ: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
