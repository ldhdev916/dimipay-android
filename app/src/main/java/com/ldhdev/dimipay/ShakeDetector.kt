package com.ldhdev.dimipay

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

class ShakeDetector(context: Context, private val onShake: () -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastUpdate = 0L
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f
    private val shakeThreshold = 800

    private var shakeCount = 0
    private val shakeTimeout = 2.seconds
    private var shakeJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        shakeJob?.cancel()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdate) > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime

                val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > shakeThreshold) {
                    handleShake()
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    private fun handleShake() {
        shakeCount++
        shakeJob?.cancel()

        shakeJob = coroutineScope.launch {
            delay(shakeTimeout)
            shakeCount = 0
        }

        if (shakeCount >= 6) {
            onShake()
            shakeJob?.cancel()
            shakeCount = 0
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}