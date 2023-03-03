package net.sgztech.timeboat.ui.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import net.sgztech.timeboat.R

/**
 * 隐私政策的弹窗
 * Created by Admin
 *Date 2022/12/29
 */
class PrivacyDialogView : AppCompatDialog{


    private var  onPrivacyListener : OnPrivacyListener ?= null
    fun setOnPrivacyListener(onListener : OnPrivacyListener){
        this.onPrivacyListener = onListener
    }

    //webView
    private var webView : WebView ?= null

    private var agree_tx : TextView ?= null
    private var no_agree_tx : TextView ?= null

    constructor(context: Context) : super (context){

    }


    constructor(context: Context,theme : Int) : super (context, theme){

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agree_dialog_fragment)

        initViews()
    }




    private fun initViews(){
        webView = findViewById(R.id.agree_content)

        agree_tx = findViewById(R.id.agree_tx)
        no_agree_tx = findViewById(R.id.no_agree_tx)

        agree_tx?.setOnClickListener {
            onPrivacyListener?.onPrivacyClick(true)
        }
        no_agree_tx?.setOnClickListener {
            onPrivacyListener?.onPrivacyClick(false)
        }


        setWebSetting()
        val url = "file:///android_asset/time_boat_privacy.html";

        //webView?.loadUrl("https://privacy-policy.sgztech.net")
        webView?.loadUrl(url)
    }

    private fun setWebSetting(){
        val settings = webView?.settings
        // 允许文件访问
        // 允许文件访问
        settings?.setAllowFileAccess(true)
        // 允许网页定位
        // 允许网页定位
        settings?.setGeolocationEnabled(true)
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        // 允许保存密码
        //settings.setSavePassword(true);
        // 开启 JavaScript
        settings?.setJavaScriptEnabled(true)
        // 允许网页弹对话框
        // 允许网页弹对话框
        settings?.setJavaScriptCanOpenWindowsAutomatically(true)
        // 加快网页加载完成的速度，等页面完成再加载图片
        // 加快网页加载完成的速度，等页面完成再加载图片
        settings?.setLoadsImagesAutomatically(true)
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        // 本地 DOM 存储（解决加载某些网页出现白板现象）
        settings?.setDomStorageEnabled(true)
        settings?.setCacheMode(WebSettings.LOAD_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
            settings?.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }
    }
}