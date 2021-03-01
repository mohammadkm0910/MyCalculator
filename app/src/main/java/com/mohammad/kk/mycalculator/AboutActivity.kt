package com.mohammad.kk.mycalculator

import android.graphics.Point
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.webkit.WebView
import android.widget.OverScroller
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(actionAbout)
        webView.loadUrl("file:///android_asset/aboutMe.html")

    }
}