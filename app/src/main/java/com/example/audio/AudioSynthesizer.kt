package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Real-time synthetic procedural sound generator using Android AudioTrack.
 * Produces crisp 8-bit/16-bit retro game sound effects and background melodies
 * without requiring large external audio asset files.
 */
object GameAudio {
    private val sampleRate = 22050
    private var isMuted = false
    private var volume = 0.7f
    private val scope = CoroutineScope(Dispatchers.Default)
    private var bgmJob: Job? = null

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            stopBgm()
        } else {
            startBgm()
        }
    }

    fun isMuted(): Boolean = isMuted

    fun toggleMute(): Boolean {
        setMuted(!isMuted)
        return isMuted
    }

    fun playButtonClick() {
        if (isMuted) return
        scope.launch {
            playTone(800.0, 35, 0.4f * volume, WaveType.SQUARE)
        }
    }

    fun playGatherPollen() {
        if (isMuted) return
        scope.launch {
            val freq = 440.0 + (Math.random() * 120.0)
            playTone(freq, 45, 0.5f * volume, WaveType.TRIANGLE)
        }
    }

    fun playConvertHoney() {
        if (isMuted) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                playTone(freq, 40, 0.45f * volume, WaveType.SINE)
                delay(30)
            }
        }
    }

    fun playLevelUp() {
        if (isMuted) return
        scope.launch {
            val notes = doubleArrayOf(440.0, 554.37, 659.25, 880.0, 1108.73)
            for (freq in notes) {
                playTone(freq, 70, 0.6f * volume, WaveType.SINE)
                delay(50)
            }
        }
    }

    fun playSlotPurchase() {
        if (isMuted) return
        scope.launch {
            playTone(300.0, 60, 0.5f * volume, WaveType.SQUARE)
            delay(50)
            playTone(600.0, 120, 0.6f * volume, WaveType.SQUARE)
        }
    }

    fun playRngRoll() {
        if (isMuted) return
        scope.launch {
            for (i in 0..6) {
                playTone(300.0 + i * 80.0, 30, 0.35f * volume, WaveType.NOISE)
                delay(40)
            }
        }
    }

    fun playRngReveal(isHighTier: Boolean) {
        if (isMuted) return
        scope.launch {
            if (isHighTier) {
                val fanfare = doubleArrayOf(523.25, 659.25, 783.99, 1046.50, 1318.51, 1567.98)
                for (freq in fanfare) {
                    playTone(freq, 110, 0.7f * volume, WaveType.SINE)
                    delay(80)
                }
            } else {
                val fanfare = doubleArrayOf(440.0, 554.37, 659.25, 880.0)
                for (freq in fanfare) {
                    playTone(freq, 80, 0.5f * volume, WaveType.TRIANGLE)
                    delay(60)
                }
            }
        }
    }

    fun playQuestComplete() {
        if (isMuted) return
        scope.launch {
            val chords = doubleArrayOf(659.25, 830.61, 987.77, 1318.51)
            for (freq in chords) {
                playTone(freq, 80, 0.5f * volume, WaveType.SINE)
                delay(50)
            }
        }
    }

    fun startBgm() {
        if (isMuted || bgmJob?.isActive == true) return
        bgmJob = scope.launch {
            // Pleasant cheerful 8-bit garden melody loop
            val melody = doubleArrayOf(
                523.25, 587.33, 659.25, 523.25,
                659.25, 587.33, 523.25, 392.00,
                440.00, 523.25, 587.33, 440.00,
                523.25, 493.88, 523.25, 659.25,
                783.99, 659.25, 587.33, 523.25,
                587.33, 659.25, 523.25, 392.00
            )
            var index = 0
            while (isActive && !isMuted) {
                val note = melody[index % melody.size]
                playTone(note, 160, 0.12f * volume, WaveType.SINE)
                delay(220)
                index++
            }
        }
    }

    fun stopBgm() {
        bgmJob?.cancel()
        bgmJob = null
    }

    private fun playTone(freq: Double, durationMs: Int, gain: Float, type: WaveType) {
        try {
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)
            val twoPi = 2.0 * Math.PI

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val rawSample: Double = when (type) {
                    WaveType.SINE -> sin(twoPi * freq * t)
                    WaveType.SQUARE -> if (sin(twoPi * freq * t) >= 0) 0.6 else -0.6
                    WaveType.TRIANGLE -> (2.0 / Math.PI) * Math.asin(sin(twoPi * freq * t))
                    WaveType.NOISE -> (Math.random() * 2.0 - 1.0)
                }
                // Gentle fade in/out envelope to prevent clicking
                val envelope = when {
                    i < 80 -> i / 80.0
                    i > numSamples - 80 -> (numSamples - i) / 80.0
                    else -> 1.0
                }
                val sampleValue = (rawSample * Short.MAX_VALUE * gain * envelope).toInt()
                buffer[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                delay(durationMs.toLong() + 50)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Audio hardware safety fallback
        }
    }

    private enum class WaveType {
        SINE, SQUARE, TRIANGLE, NOISE
    }
}
