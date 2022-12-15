package com.device.ui.baseUi.baseFragment

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.TextView
import com.device.ui.R


class CommonLoadingDialog : Dialog {

    private var tv_loadingmsg: TextView? = null

    constructor(context: Context, content: String? = null) : super(context, R.style.dialog){
        initView(content)
    }

    private fun initView(content: String?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.common_progress_dialog)
        tv_loadingmsg = findViewById<View>(R.id.tv_loadingmsg) as TextView
        setLoadingMsg(content)
    }

     fun reloadMessage(message:String){
        tv_loadingmsg?.text =message
    }


    /**
     * 设置提示语
     * @param content
     */
    fun setLoadingMsg(content: String?) {
        if (TextUtils.isEmpty(content)) {
            tv_loadingmsg!!.visibility = View.VISIBLE
        } else {
            tv_loadingmsg!!.visibility = View.VISIBLE
            tv_loadingmsg!!.text = content
        }
    }
}