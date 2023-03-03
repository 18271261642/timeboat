package net.sgztech.timeboat.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.*
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.ActivityWebviewBinding
import java.lang.Appendable

class WebViewActivity :BaseActivity(){
    private val  webViewBinding :ActivityWebviewBinding by viewBinding()
    private var webViewType =1
    private lateinit var webUrl :String
    companion object{
        var serverAgree =1
        var privateAgree = 2
        var appHistory = 3
        var cancelAgree = 4
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_webview
    }

    override fun initBindView() {


        webViewBinding.webViewTitle.backArrow.setOnClickListener(this)

    }

    override fun initData() {
        super.initData()
        webViewType = intent.getIntExtra(Constants.WEB_VIEW_URL_TYPE,1)

        if(webViewType ==serverAgree){
            //服务协议
            webViewBinding.webViewTitle.titleName.text ="服务协议"
            webUrl ="https://user-agreement.sgztech.net/"
        }else if(webViewType ==privateAgree){
            webViewBinding.webViewTitle.titleName.text ="隐私政策"
           // webUrl ="https://privacy-policy.sgztech.net"

            webUrl =  "file:///android_asset/time_boat_privacy.html"
        }else if(webViewType ==appHistory){
            webUrl = intent.getStringExtra(Constants.WEB_VIEW_URL)!!
            webViewBinding.webViewTitle.titleName.text ="更新记录"
        }else if(webViewType == cancelAgree){
            webUrl = intent.getStringExtra(Constants.WEB_VIEW_URL)!!
            webViewBinding.webViewTitle.titleName.text ="注销协议"
        }

        if(webUrl!=null){
            configWebview(webUrl)
        }
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        if(v.id==R.id.back_arrow){
            finish()
        }
    }

    /**
     * 配置webview
     */
    @SuppressLint("JavascriptInterface")
    private fun configWebview(urlData: String?) {
        if (null == urlData || urlData.length == 0) {
            return
        }
        val webSettings: WebSettings = webViewBinding.agreeContent.getSettings()
        // 设置支持JavaScript
        webSettings.javaScriptEnabled = true
        // 设置可以访问文件
        webSettings.allowFileAccess = true
        // 设置支持缩放
        // webSettings.setBuiltInZoomControls(true);
        // html5必须设置
//        webSettings.domStorageEnabled = true

        // 设置获得焦点
//        webView.setFocusable(false);
        webViewBinding.agreeContent.getSettings().setBuiltInZoomControls(false)
        webViewBinding.agreeContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        webViewBinding.agreeContent.setVerticalScrollBarEnabled(false)
        webViewBinding.agreeContent.setHorizontalScrollBarEnabled(false)
        webViewBinding.agreeContent.setScrollContainer(false)
        webViewBinding.agreeContent.loadUrl(urlData)
        webViewBinding.agreeContent.setBackgroundColor(resources.getColor(R.color.white))
        webViewBinding.agreeContent.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {

                } else {

                }
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)
            }
        })
        webViewBinding.agreeContent.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)

            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }
        })

    }
}