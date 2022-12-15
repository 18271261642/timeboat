package net.sgztech.timeboat.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.DialogFragment
import com.device.ui.viewBinding.viewBinding
import net.sgztech.timeboat.R
import net.sgztech.timeboat.databinding.AgreeDialogFragmentBinding

class AgreeDialogFragment:DialogFragment(),View.OnClickListener
{
    private val agreeBinding :AgreeDialogFragmentBinding by viewBinding()
    private var agreeListen :AgreeListen? =null
    private var webUrl :String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog)
        this.isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.agree_dialog_fragment, null)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        agreeBinding.agreeTx.setOnClickListener(this)
        agreeBinding.noAgreeTx.setOnClickListener(this)
//        val bundle = arguments
//        if (bundle != null) {
//            val url = bundle["webUrl"] as String
            configWebview(webUrl)
//        }
    }
    fun updateArg(url :String){
        webUrl =url
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.agree_tx->{

                if(agreeListen!=null){
                    agreeListen!!.agree(true)
                }
                dismissAllowingStateLoss()
            }
            R.id.no_agree_tx->{
                if(agreeListen!=null){
                    agreeListen!!.agree(false)
                }
                dismissAllowingStateLoss()
            }
        }
    }

    interface  AgreeListen{
        fun agree(agree: Boolean)
    }

    fun  setAgreeListen(listen :AgreeListen){
        this.agreeListen  = listen
    }

    /**
     * 配置webview
     */
    @SuppressLint("JavascriptInterface")
    private fun configWebview(urlData: String?) {
        if (null == urlData || urlData.length == 0) {
            return
        }
        val webSettings: WebSettings = agreeBinding.agreeContent.getSettings()
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
        agreeBinding.agreeContent.getSettings().setBuiltInZoomControls(false)
        agreeBinding.agreeContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        agreeBinding.agreeContent.setVerticalScrollBarEnabled(false)
        agreeBinding.agreeContent.setHorizontalScrollBarEnabled(false)
        agreeBinding.agreeContent.setScrollContainer(false)
        agreeBinding.agreeContent.loadUrl(urlData)
        agreeBinding.agreeContent.setBackgroundColor(resources.getColor(R.color.white))
        agreeBinding.agreeContent.setWebChromeClient(object : WebChromeClient() {
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
        agreeBinding.agreeContent.setWebViewClient(object : WebViewClient() {
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