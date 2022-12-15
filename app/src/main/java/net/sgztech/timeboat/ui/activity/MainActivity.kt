package net.sgztech.timeboat.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import com.device.ui.baseUi.baseActivity.ActivityStack
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.QUITE_CANCEL_ACCOUNT
import net.sgztech.timeboat.config.Constants.Companion.QUITE_TO_HOME_PAGE
import net.sgztech.timeboat.databinding.ActivityMainBinding
import net.sgztech.timeboat.managerUtlis.BleServiceManager
import net.sgztech.timeboat.managerUtlis.FragmentSwitchManager
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.ExitAccountStatusEvent
import net.sgztech.timeboat.provide.dataModel.LoginStatusEvent
import net.sgztech.timeboat.provide.viewModel.MainActivityViewModel
import net.sgztech.timeboat.ui.fragment.*
import net.sgztech.timeboat.ui.fragment.LoginDialogFragment.Companion.authorLoginSuccessAction
import net.sgztech.timeboat.ui.fragment.LoginDialogFragment.Companion.bindPhoneAction
import net.sgztech.timeboat.ui.fragment.LoginDialogFragment.Companion.improveInformationAction
import net.sgztech.timeboat.ui.utils.UIUtils
import net.sgztech.timeboat.util.*
import org.greenrobot.eventbus.EventBus

class MainActivity : BaseActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val mainBinding: ActivityMainBinding by viewBinding()
    private val mainViewModel :MainActivityViewModel by viewModels()
    var netRequest =true
//    var wifiNetRequest = true
    private var agreeDialog: AgreeDialogFragment? = null
    private var homePageFragment: BaseFragment? = null
    private var deviceFragment: BaseFragment? = null
    private var aboutMeFragment: BaseFragment? = null
    private var frgSwitchManager: FragmentSwitchManager? = null
    private var loginDialogFragment: LoginDialogFragment? = null
    private var bindingPhoneDialogFragment: BindingPhoneDialogFragment? = null
    private lateinit var selectPageRadio: RadioButton
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    companion object {
        var loginAction = 1000
        var bindingPhoneAction = 2000

    }


    override fun initBindView() {
        mainBinding.glxMenuHome.setOnClickListener(this)
        mainBinding.glxMenuDevice.setOnClickListener(this)
        mainBinding.glxMenuMine.setOnClickListener(this)
    }


    override fun initData() {
        frgSwitchManager = FragmentSwitchManager(this)
        homePageFragment = HomeFragment().newInstance()
        deviceFragment = DeviceFragment().newInstance()
        aboutMeFragment = AboutMeFragment().newInstance()
        frgSwitchManager!!.add(deviceFragment).add(aboutMeFragment).add(homePageFragment).commit();
        mainBinding.glxMenuHome.isChecked = true
        selectPageRadio = mainBinding.glxMenuHome
        if (SettingInfoManager.instance.isFirstIntoApp()) {
            showAgree()
        }
        netRequest = checkNetWorkPermission()
        if(netRequest){
           if(!UIUtils.isNetworkAvailable(this)) {
               UToast.showShortToast("手机未联网,请打开网络")
           }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.configData()
    }

    override fun startObserve() {
        mainViewModel.configData.vmObserver(this){

            onAppLoading = {
                LogUtil.d(TAG ,"开始")
            }

            onAppSuccess = {
                if(it!=null){
                    if (it.versionExpired==1){
                        showAppExpiredDialog()
                    }
                    if(it.bleName.isNotEmpty()){
                        SettingInfoManager.instance.bleNameList = it.bleName
                    }
                    if(it.tcpHost.isNotEmpty()){
                        SettingInfoManager.instance.tcpHost =it.tcpHost
                    }
                    if(it.tcpPort.isNotEmpty()){
                        SettingInfoManager.instance.tcpPort = it.tcpPort
                    }
                    if(it.heartBeatInterval!=0){
                        SettingInfoManager.instance.changeHeartBeatInterval(it.heartBeatInterval)
                    }

                }
            }

            onAppComplete ={

            }

            onAppError = { msg,errorCode->

            }

        }
    }

    override fun initDataFromThread() {
        super.initDataFromThread()
        SettingInfoManager.instance.checkLocalInfo()
    }

    override fun threadDataInit() {
        super.threadDataInit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            if (intent.getStringExtra(QUITE_CANCEL_ACCOUNT).equals(QUITE_TO_HOME_PAGE)) {
                //更新数据
                SettingInfoManager.instance.checkLocalInfo()
                //蓝牙断开连接
                BleServiceManager.instance.disconnectAndClean()
                jumpHomePage()
                EventBus.getDefault().post(ExitAccountStatusEvent())
            }
        }
    }

    private fun jumpHomePage() {
        frgSwitchManager!!.showFragment(homePageFragment)
        selectPageRadio = mainBinding.glxMenuHome
        mainBinding.glxMenuHome.isChecked = true
        mainBinding.glxMenuMine.isChecked = false
        mainBinding.glxMenuMine.isChecked = false
    }

    override fun widgetClick(v: View) {

        onClickEvent(v)

//        if (SettingInfoManager.instance.isAccessTokenNotEmpty()) {
//            if (SettingInfoManager.instance.isPhoneEmpty()) {
//                showBinding()
//            } else {
//                super.widgetClick(v)
//                onClickEvent(v)
//            }
//
//        } else {
//            showLogin()
//            resetSelectButton()
//        }
    }

//    private fun resetSelectButton() {
//        selectPageRadio.isChecked = true
//    }


    fun onClickEvent(v: View) {
        when (v.id) {
            R.id.glx_menu_home -> {
                jumpHomePage()
            }
            R.id.glx_menu_device -> {
                frgSwitchManager!!.showFragment(deviceFragment)
                selectPageRadio = mainBinding.glxMenuDevice
                mainBinding.glxMenuDevice.isChecked = true
                mainBinding.glxMenuHome.isChecked = false
                mainBinding.glxMenuMine.isChecked = false
            }
            R.id.glx_menu_mine -> {
                frgSwitchManager!!.showFragment(aboutMeFragment)
                selectPageRadio = mainBinding.glxMenuMine
                mainBinding.glxMenuMine.isChecked = true
                mainBinding.glxMenuDevice.isChecked = false
                mainBinding.glxMenuHome.isChecked = false
            }
            else -> {


            }
        }

    }

    fun showLogin() {
        if (loginDialogFragment == null) {
            loginDialogFragment = LoginDialogFragment()
        } else {
            loginDialogFragment = null
            loginDialogFragment = LoginDialogFragment()
        }
        loginDialogFragment!!.setLoginListen(object : LoginDialogFragment.LoginStatusListen {
            override fun login(type: Int, login: Boolean, data: Any?) {
                if (login) {
                    when (type) {
                        improveInformationAction -> {
                            val intent =
                                Intent(this@MainActivity, ImproveInformationActivity::class.java)
                            startActivity(intent)
                        }
                        bindPhoneAction -> showBinding()
                        authorLoginSuccessAction -> {
                            (homePageFragment as HomeFragment).getDeviceListData()
                            EventBus.getDefault().post(LoginStatusEvent(authorLoginSuccessAction))
                        }
                    }
                } else {
                    //Toast.makeText(this@MainActivity,"登录失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDismiss() {

            }
        })

        val fm = this.supportFragmentManager
        if (!loginDialogFragment!!.isAdded && null == fm.findFragmentByTag("loginAuthor")) {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(loginDialogFragment!!, "loginAuthor")
            ft.commitAllowingStateLoss()
        } else if (loginDialogFragment!!.isHidden || !loginDialogFragment!!.isVisible) {
            val ft = fm.beginTransaction()
            ft.show(loginDialogFragment!!).commitNowAllowingStateLoss()
        } else {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(loginDialogFragment!!, "loginAuthor")
            ft.commitAllowingStateLoss()
        }
    }


    fun showBinding() {
        if (bindingPhoneDialogFragment == null) {
            bindingPhoneDialogFragment = BindingPhoneDialogFragment()
        } else {
            bindingPhoneDialogFragment = null
            bindingPhoneDialogFragment = BindingPhoneDialogFragment()
        }
        bindingPhoneDialogFragment!!.setBindListen(object :
            BindingPhoneDialogFragment.BindStatusListen {
            override fun login(type: Int, login: Boolean, data: Any?) {
                if (login) {
                    when (type) {
                        improveInformationAction -> {
                            val intent =
                                Intent(this@MainActivity, ImproveInformationActivity::class.java)
                            startActivity(intent)
                        }
                        bindPhoneAction -> showBinding()
                        authorLoginSuccessAction -> {
                            (homePageFragment as HomeFragment).getDeviceListData()
                        }
                        else -> {
                            Toast.makeText(this@MainActivity, "绑定手机号失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "绑定手机号失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDismiss() {

            }
        })

        val fm = this.supportFragmentManager
        if (!bindingPhoneDialogFragment!!.isAdded && null == fm.findFragmentByTag("bindingPhone")) {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(bindingPhoneDialogFragment!!, "bindingPhone")
            ft.commitAllowingStateLoss()
        } else if (bindingPhoneDialogFragment!!.isHidden || !bindingPhoneDialogFragment!!.isVisible) {
            val ft = fm.beginTransaction()
            ft.show(bindingPhoneDialogFragment!!).commitNowAllowingStateLoss()
        } else {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(bindingPhoneDialogFragment!!, "bindingPhone")
            ft.commitAllowingStateLoss()
        }
    }



    /**
     * 返回按键点击间隔
     */

    private var exitTime = 0L

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                exitTime = System.currentTimeMillis()
                UToast.showLongToast("再按一次退出" + getString(R.string.app_name))
            } else {
                ActivityStack.create().finishAllActivity()
                return super.onKeyDown(keyCode, event)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun getFrgManager(): FragmentSwitchManager? {
        return frgSwitchManager
    }


    fun showAgree() {
        if (agreeDialog == null) {
            agreeDialog = AgreeDialogFragment()
        }
        agreeDialog!!.updateArg(" https://privacy-policy.sgztech.net")
        agreeDialog!!.setAgreeListen(object : AgreeDialogFragment.AgreeListen {
            override fun agree(agree: Boolean) {
                if (agree) {
                    SharedPreferencesUtil.getInstance().setBoolean(Constants.FIRST_INTO_APP, false)
                } else {
                    UToast.showLongToast("请注意:拒绝协议将导致功能不能使用!!")
                    System.exit(0)
                }
            }
        })

        val fm = this.supportFragmentManager
        if (!agreeDialog!!.isAdded && null == fm.findFragmentByTag("agree_dialog")) {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(agreeDialog!!, "agree_dialog")
            ft.commitAllowingStateLoss()
        } else if (agreeDialog!!.isHidden || !agreeDialog!!.isVisible) {
            val ft = fm.beginTransaction()
            ft.show(agreeDialog!!).commitNowAllowingStateLoss()
        } else {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(agreeDialog!!, "agree_dialog")
            ft.commitAllowingStateLoss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == Constants.PERMISSIONS_WIFI_STATE && grantResults.size>0 &&grantResults[0] != PackageManager.PERMISSION_GRANTED){
//            UToast.showShortToast("您没有打开WIFI授权，请在设置中打开")
//        }
//
        if(requestCode == Constants.PERMISSIONS_NETWORK_STATE && grantResults.size>0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            UToast.showShortToast("您没有打开网络授权，请在设置中打开")
        }

        // 获取到Activity下的Fragment
        val fragments = supportFragmentManager.fragments ?: return
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (fragment in fragments) {
            fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showAppExpiredDialog(){
        AlertDialog.Builder(this).apply {
            setMessage("此版本已过期，请下载最新的app版本")
            setPositiveButton("确定") { dialog, which ->
                dialog.dismiss()
                finish()
            }
            setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
                finish()
            }
            create()
        }.setCancelable(false).show()
    }

}