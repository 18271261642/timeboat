package com.device.ui.baseUi.baseFragment

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.device.ui.R

class CommonConfirmDialog : Dialog {
    private var tv_title :TextView
    private var tv_content :TextView
    private var  btn_single : Button
    private var  btn_confirm :Button
    private var  btn_cancel :Button
    private var  v_single_line :View

    constructor(context: Context, title: String = "", content: String = "您确定这么做吗", okClick: () -> Unit = {}, cancleClick: () -> Unit = {}  ): super(context, R.style.dialog){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.common_confirm_dialog)
        this.setCanceledOnTouchOutside(true)
        tv_title =findViewById(R.id.tv_title)
        tv_content =findViewById(R.id.tv_content)
        btn_single =findViewById(R.id.btn_single)
        btn_confirm =findViewById(R.id.btn_confirm)
        btn_cancel =findViewById(R.id.btn_cancel)
        v_single_line =findViewById(R.id.v_single_line)
        tv_title.visibility = View.GONE
        btn_single.visibility = View.GONE

        title.isNotEmpty().let {
            setTitle(title)
        }
        setContent(content)

        btn_confirm.setOnClickListener { okClick();dismiss() }
        btn_cancel.setOnClickListener { cancleClick();dismiss() }
        btn_single.setOnClickListener { okClick();dismiss() }
    }


    /**
     * 设置标题
     * @param str
     * @return
     */
    fun setTitle(str: String?): CommonConfirmDialog? {
        tv_title.visibility = View.VISIBLE
        tv_title.text = str
        return this
    }

    /**
     * 设置内容
     * @param str
     * @return
     */
    fun setContent(str: String?): CommonConfirmDialog? {
        tv_content.text = str
        return this
    }


    /**
     * 设置取消按钮文字
     * @param str
     * @return
     */
    fun setCancleBtnText(str: String?): CommonConfirmDialog? {
        btn_cancel.setText(str)
        return this
    }

    /**
     * 设置确定按钮文字
     * @param str
     * @return
     */
    fun setConfirmBtnText(str: String?): CommonConfirmDialog? {
        btn_confirm.setText(str)
        return this
    }

    /**
     * 设置单按钮文字
     */
    fun setSingleBtnText(str: String?): CommonConfirmDialog? {
        btn_single.setText(str)
        return this
    }

    /**
     * 设置为单个按钮
     */
    fun setSingleBtnVisible(visible: Boolean): CommonConfirmDialog? {
        btn_single.visibility = if (visible) View.VISIBLE else View.GONE
        btn_cancel.visibility = if (!visible) View.VISIBLE else View.GONE
        btn_confirm.visibility = if (!visible) View.VISIBLE else View.GONE
        v_single_line.visibility = if (!visible) View.VISIBLE else View.GONE
        return this
    }

    fun setOkClick(click: () -> Unit){
        btn_confirm.setOnClickListener { click();dismiss() }
    }

    fun setCancel(click: () -> Unit){
        btn_cancel.setOnClickListener { click();dismiss() }
    }

}