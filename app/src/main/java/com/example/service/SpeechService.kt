package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechService(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let {
                val result = it.setLanguage(Locale.getDefault())
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = true
                }
            }
        }
    }

    fun speakName(name: String) {
        if (isInitialized && name.isNotBlank()) {
            tts?.speak(name, TextToSpeech.QUEUE_FLUSH, null, "contact_speech_id")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
