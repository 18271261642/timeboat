package com.device.ui.baseUi.baseActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.device.ui.baseUi.baseFragment.CommonLoadingDialog
import com.device.ui.baseUi.interfaces.I_BaseActivity
import com.device.ui.ext.putExtras
import com.device.ui.ext.startActivity
import com.device.ui.utils.StatusBarUtil.setLightMode
import com.device.ui.utils.StatusBarUtil.setLightModeNotFull
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import kotlinx.coroutines.*

abstract class  BaseActivity: AppCompatActivity(), I_BaseActivity,View.OnClickListener {

    @JvmField
    var aty: Activity? = null
    var dialog: CommonLoadingDialog? = null
    /**
     * Activity状态
     */
    private var activityState = I_BaseActivity.CREATE

    override fun onCreate(savedInstanceState: Bundle?) {
        aty = this
        activityState = I_BaseActivity.CREATE
        ActivityStack.create().addActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initBindView()
        startObserve()
        initializer()
    }

    abstract fun getLayoutId(): Int

    abstract fun initBindView()



    // 仅仅是为了代码整洁点
    private fun initializer() {

        lifecycleScope.launch {

           withContext(Dispatchers.IO) {
                initDataFromThread()
            }
            threadDataInit()

        }
        setLightModeNotFull(this)
        initData()

        initWidget()

    }

    override fun startObserve() {
    }
    override fun initData() {}
    override fun initWidget() {}

    override fun initDataFromThread(){

    }

    protected open fun threadDataInit() {

    }

    /**
     * listened widget's click method
     */
    override fun widgetClick(v: View) {}
    override fun onClick(v: View) {
        widgetClick(v)
    }


    protected fun bindClickView(view: View){
        view.setOnClickListener(this)
    }

    protected open fun <T : View> bindView(id: Int): T? {

        return findViewById<T>(id)
    }

    protected open fun <T : View> bindClickView(id: Int, click: Boolean): T? {
        val view: T? = findViewById<T>(id)
        if (click&&view!=null) {
            view.setOnClickListener(this)
        }
        return view
    }

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        lifecycleScope.launch { block() }

    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    override fun onResume() {
        super.onResume()
        activityState = I_BaseActivity.RESUME
    }

    override fun onPause() {
        super.onPause()
        activityState = I_BaseActivity.PAUSE
    }

    override fun onStop() {
        super.onStop()
        activityState = I_BaseActivity.STOP
    }


    override fun onDestroy() {

        activityState = I_BaseActivity.DESTROY
        super.onDestroy()
        ActivityStack.create().finishActivity(this)
        aty = null

    }

    fun isActivityDestroyed(): Boolean {
        if (aty == null || aty!!.isFinishing) {
           return true
        } else {
            return  activityState == I_BaseActivity.DESTROY
        }
    }

     fun showLoadingDialog(content: String) {
        dialog?:let {
            dialog = CommonLoadingDialog(this, content)
        }
        dialog?.show()
    }

     fun dismissLoadingDialog() {
        dialog?.dismiss()
    }



    inline fun <reified TARGET : FragmentActivity> exitOutAccount(){
       //此处使用Constants 中的静态常量 USER_INFO QUITE_ACCOUNT BLE_DEVICE_INFO
        launchOnUI {
            SharedPreferencesUtil.getInstance().setString("user_info","")
            SharedPreferencesUtil.getInstance().setString("ble_device_info","")
        }
        val intent = Intent(this, TARGET::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("quite_cancel_account", "quite_home_page")
        startActivity(intent)
    }

}
