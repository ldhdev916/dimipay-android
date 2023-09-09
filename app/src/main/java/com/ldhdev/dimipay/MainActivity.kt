package com.ldhdev.dimipay

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ldhdev.dimipay.ui.theme.DimipayTheme

private const val DIMIPAY_URL = "https://dimipay.ldhdev.com"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

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

                    AndroidView(
                        factory = {
                            WebView(it).apply {

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
                            it.loadUrl(DIMIPAY_URL)
                        }
                    )
                }
            }
        }
    }
}