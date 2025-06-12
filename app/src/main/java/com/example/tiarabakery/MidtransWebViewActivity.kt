package com.example.tiarabakery

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MidtransWebViewActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient() // agar tidak buka browser eksternal

        val snapUrl = intent.getStringExtra("snap_url")
        if (snapUrl != null) {
            webView.loadUrl(snapUrl)
        } else {
            Toast.makeText(this, "URL pembayaran tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
