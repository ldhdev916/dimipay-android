package com.ldhdev.dimipay

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ldhdev.dimipay.ui.theme.DimipayTheme

private const val DIMIPAY_URL = "https://dimipay.ldhdev.com"

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(getSystemService(NOTIFICATION_SERVICE) as NotificationManager) {
            createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "흔들림 감지",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )

            if (!areNotificationsEnabled()) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

            startActivity(intent)
        }

        scheduleDimipayAlarm()

        setContent {

            var currentWebView by remember { mutableStateOf<WebView?>(null) }

            DimipayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    DisposableEffect(this) {
                        val layoutParams = window.attributes
                        layoutParams.screenBrightness =
                            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                        window.attributes = layoutParams
                        onDispose {
                            layoutParams.screenBrightness =
                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                            window.attributes = layoutParams
                        }

                    }

                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { currentWebView?.reload() }
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "새로고침")
                            }
                        }
                    ) { innerPadding ->
                        AndroidView(
                            modifier = Modifier.padding(innerPadding),
                            factory = {
                                WebView(it).apply {
                                    currentWebView = this

                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

                                    webViewClient = WebViewClient()
                                    webChromeClient = WebChromeClient()

                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true

                                    loadUrl(DIMIPAY_URL)
                                }
                            },
                            update = {
                                currentWebView = it
                                it.loadUrl(DIMIPAY_URL)
                            }
                        )
                    }
                }
            }
        }
    }
}