package com.device.ui.baseUi.baseFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseFragment : androidx.fragment.app.Fragment(),View.OnClickListener  {
    private var TAG = BaseFragment::class.java.simpleName
    private var fragmentRootView: View? = null
    private var label: String? = null
    private var  isMainPage :Boolean = false //是否是主页
    lateinit var mContext: Context
    var dialog: CommonLoadingDialog? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
          fragmentRootView = inflater.inflate(getLayoutResId(), container, false)
        return fragmentRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindView()
        initializer()
    }

    // 仅仅是为了代码整洁点
    private fun initializer() {

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                initDataFromThread()
            }
            threadDataInit()

        }
        initData()


    }

    open fun initDataFromThread(){

    }

    protected open fun threadDataInit() {

    }





    fun isMainPage(): Boolean {
        return isMainPage
    }

    fun setMainPage(mainPage: Boolean) {
        isMainPage = mainPage
    }

    open fun setLabel(label: String) {
        this.label = label
    }

    open fun getLabel() :String? {
        return  label
    }

    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {

        lifecycleScope.launch { block() }

    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    abstract fun getLayoutResId(): Int

    abstract fun initBindView()

    abstract fun initData()

    /**
     * listened widget's click method
     */
    open fun widgetClick(v: View) {}
    override fun onClick(v: View) {
        widgetClick(v)
    }

    protected fun bindClickView(view: View){
        view.setOnClickListener(this)
    }

    protected open fun <T : View> bindView(id: Int): T? {

        return fragmentRootView!!.findViewById<T>(id)
    }

    protected open fun <T : View> bindClickView(id: Int, click: Boolean): T? {
        val view: T? = fragmentRootView!!.findViewById<T>(id)
        if (click&&view!=null) {
            view.setOnClickListener(this)
        }
        return view
    }

    /**
     * 当通过changeFragment()显示时会被调用(类似于onResume)
     */
    open fun onChange() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun showLoadingDialog(content: String) {
        dialog?:let {
            dialog = CommonLoadingDialog(mContext, content)
        }
        dialog?.show()
    }

    fun dismissLoadingDialog() {
        dialog?.dismiss()
    }

    fun reloadingMessage(message :String){
        dialog?:let {
            dialog = CommonLoadingDialog(mContext, message)
        }
        dialog?.reloadMessage(message)
        dialog?.show()
    }

    inline fun <reified TARGET : FragmentActivity> exitOutAccount(){
        //此处使用Constants 中的静态常量 USER_INFO QUITE_ACCOUNT
        launchOnUI {
            SharedPreferencesUtil.getInstance().setString("user_info","")
        }
        val intent = Intent(activity, TARGET::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("quite_account", "quite_home_page")
        startActivity(intent)
    }

}