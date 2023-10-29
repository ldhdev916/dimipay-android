package com.ldhdev.dimipay

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class ShakeDetectionService : Service() {

    private val shakeDetector by lazy {
        ShakeDetector(this) {
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)

            stopSelf()
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .build()

        startForeground(1, notification)

        shakeDetector.start()

        scope.launch {
            delay(20.minutes)
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {

        scheduleDimipayAlarm()

        shakeDetector.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)

        super.onDestroy()
    }
}