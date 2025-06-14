package com.example.tiarabakery

import android.os.Build
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MidtransWebView(url: String, onFinish: () -> Unit) {
    val alreadyHandled = remember { mutableStateOf(false) }

    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url != null && (url.contains("status_code=200") || url.contains("finish"))) {
                        if (!alreadyHandled.value) {
                            alreadyHandled.value = true
                            onFinish()
                        }
                        return true
                    }
                    return false
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val currentUrl = request?.url.toString()
                    if ((currentUrl.contains("status_code=200") || currentUrl.contains("finish"))) {
                        if (!alreadyHandled.value) {
                            alreadyHandled.value = true
                            onFinish()
                        }
                        return true
                    }
                    return false
                }
            }

            webChromeClient = WebChromeClient()

            loadUrl(url)
        }
    })
}



