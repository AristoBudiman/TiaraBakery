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
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MidtransWebView(url: String, onFinish: () -> Unit) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true

            webViewClient = object : WebViewClient() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val currentUrl = request?.url.toString()
                    Log.d("MIDTRANS", "Navigating to: $currentUrl")

                    // Deteksi pembayaran selesai
                    if (currentUrl.contains("status_code=200") || currentUrl.contains("finish")) {
                        onFinish()
                        return true
                    }
                    return false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    Log.d("MIDTRANS", "Navigating to: $url")
                    if (url != null && (url.contains("status_code=200") || url.contains("finish"))) {
                        onFinish()
                        return true
                    }
                    return false
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    Log.d("MIDTRANS", "Progress: $newProgress%")
                    super.onProgressChanged(view, newProgress)
                }
            }

            loadUrl(url)
        }
    })
}



