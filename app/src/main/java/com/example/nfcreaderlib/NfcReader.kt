package com.example.nfcreaderlib

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.nfc.*
import android.os.*
import androidx.annotation.RequiresApi

object NfcReader {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(context: Context, callback: NfcCallback) {
        val appContext = context.applicationContext
        val serviceIntent = Intent(appContext, NfcReaderService::class.java)
        NfcReaderService.callback = callback
        appContext.startForegroundService(serviceIntent)

        nfcAdapter = NfcAdapter.getDefaultAdapter(appContext)
        if (nfcAdapter == null) {
            callback.onFailure("Thiết bị không hỗ trợ NFC.")
            return
        }

        pendingIntent = PendingIntent.getService(
            appContext, 0, serviceIntent, PendingIntent.FLAG_MUTABLE
        )
    }

    fun enableForegroundDispatch(activity: Activity) {
        nfcAdapter?.enableForegroundDispatch(
            activity,
            pendingIntent,
            null,
            null
        )
    }

    fun disableForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }
}
