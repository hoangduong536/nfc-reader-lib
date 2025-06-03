package com.example.nfcreaderlib

interface NfcCallback {
    fun onProcessing()
    fun onSuccess(tagData: String)
    fun onFailure(errorMessage: String)
}
