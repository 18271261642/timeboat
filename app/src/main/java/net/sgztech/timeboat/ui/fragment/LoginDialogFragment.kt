package net.sgztech.timeboat.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.device.ui.baseUi.baseFragment.CommonLoadingDialog
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants.Companion.PHONE_NUMBER
import net.sgztech.timeboat.config.Constants.Companion.WEB_VIEW_URL_TYPE
import net.sgztech.timeboat.databinding.LoginFragmentBinding
import net.sgztech.timeboat.managerUtlis.StringsUtils.isPhone
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.provide.viewModel.LoginViewModel
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.loginAction
import net.sgztech.timeboat.ui.activity.WebViewActivity

class LoginDialogFragment : DialogFragment(),View.OnClickListener {
    private val TAG =LoginDialogFragment::class.java.simpleName
    private  val loginBinding :LoginFragmentBinding by viewBinding()
    private var checkAgree =false
    private var statusListen :LoginStatusListen? =null
    private var authorCodeDialogFragment :AuthorCodeDialogFragment ? =null
//    private var agreeDialog : AgreeDialogFragment? = null
    private val loginModel : LoginViewModel by viewModels()
    var customdialog: CommonLoadingDialog? = null
    private lateinit var mContext: Context
    companion object{
        // 返回按键
        val closeLogin = 0
        //验证码
        val authorCodeLogin = 1
        //微信登录
        val wechatLogin = 2

        val improveInformationAction =1001
        val bindPhoneAction =1002
        val authorLoginSuccessAction=1003
        val closeBinding=1004
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog)

        this.isCancelable = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.login_fragment, null)

        return mView
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置 dialog 的背景为 null
//        dialog!!.window!!.setBackgroundDrawable(null);
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding.checkBox.setOnClickListener(this)
        loginBinding.loginCheckCodeDo.setOnClickListener(this)
        loginBinding.loginWx.setOnClickListener(this)
        loginBinding.closeDialog.setOnClickListener(this)
        loginBinding.agreeSecretInfo.setOnClickListener(this)
        loginBinding.agreeServicesInfo.setOnClickListener(this)
        loginBinding.loginPhoneNum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                    if(p0!=null&&p0.length>=11){
                        loginBinding.loginCheckCodeDo.isEnabled =true
                    }
            }

        })
        loginModel.loginWxData.vmObserver(this){
            onAppLoading = {
                Log.d(TAG ,"loginWxData 开始")

            }
            onAppSuccess = {
                //获取用户信息成功
                LogUtil.d(TAG ,"loginWxData it.headImgUrl " +it?.headImgUrl + "，it.openId" + it?.openId + "，it.nickname=" +it?.nickname)
                if(it!=null){
                    loginModel.login(wechatLogin ,"" ,"" ,it.headImgUrl,it.openId, it.nickname)
                }

            }
            onAppError = { msg, _ ->
                UToast.showShortToast(msg)
                LogUtil.d(TAG , "loginWxData error =" + msg)
            }
            onAppComplete = {
                Log.d(TAG ,"loginWxData onAppComplete 完成")

            }
        }

        loginModel.userLoginData.vmObserver(this){
            onAppLoading = {
                Log.d(TAG ,"userLoginData 开始")
                showLoadingDialog("")
            }
            onAppSuccess = {
                if(it!=null&&it.phone!=null && it.phone!!.isNotEmpty() && it.isNewUser==1){
                 if(statusListen!=null){
                    statusListen!!.login(improveInformationAction,true,it)
                    statusListen!!.onDismiss()

                  }
                }else if(it!=null&&it.phone!=null && it.phone!!.isNotEmpty()&&it.isNewUser==0){

                    if(statusListen!=null){
                        statusListen!!.login(authorLoginSuccessAction,true,it)
                        statusListen!!.onDismiss()

                    }
                    UToast.showShortToast("登录成功")

                }else if(it!=null&&it.phone==null ||
                    (it!=null&&it.phone!=null &&it.phone!!.isEmpty())){
                    //微信无手机号，执行绑定操作
                    if(statusListen!=null){
                        statusListen!!.login(bindPhoneAction,true,it)
                        statusListen!!.onDismiss()
                    }
                }
            }
            onAppError = {
                msg ,errorCode->
                dismissLoadingDialog()
                dismissAllowingStateLoss()
                UToast.showShortToast("登录失败")
                Log.d(TAG , "userLoginData error =" + msg)
            }
            onAppComplete = {
                Log.d(TAG ,"onAppComplete 完成")
                dismissLoadingDialog()
                dismissAllowingStateLoss()
            }
        }


    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.check_box -> {
                if(checkAgree){
                    loginBinding.checkBox.isChecked =false
                    checkAgree =false

                }else{
                    loginBinding.checkBox.isChecked =true
                    checkAgree = true
                }
            }

            R.id.login_check_code_do->{
                checkCodeClick()
            }

            R.id.login_wx->{
                loginModel.registerWx()
//                Toast.makeText(activity,"微信登录暂未开通，正审核中",Toast.LENGTH_SHORT).show()
            }

            R.id.close_dialog -> {
                if(statusListen!=null){
                    statusListen!!.login(closeLogin,false,null)
                    statusListen!!.onDismiss()
                }
                dismissAllowingStateLoss()
            }

            R.id.agree_services_info ->{
                showAgreeType(1)
            }

            R.id.agree_secret_info ->{
                showAgreeType(2)
            }
        }
    }

    fun showAgreeType(type :Int){
        val intent = Intent(activity ,WebViewActivity::class.java)
        intent.putExtra(WEB_VIEW_URL_TYPE, type)
        activity?.startActivity(intent)
    }


    fun checkCodeClick() {
        val phone =loginBinding.loginPhoneNum.text.toString().trim()
        if (phone.isEmpty()) {
            Toast.makeText(activity,"请输入电话号码",Toast.LENGTH_SHORT).show()
            return
        }

        if(!isPhone(phone)){
            Toast.makeText(activity,"请输入有效的电话号码",Toast.LENGTH_SHORT).show()
            return
        }

        if(!checkAgree){
            Toast.makeText(activity,"请先阅读并同意服务协议",Toast.LENGTH_SHORT).show()
            return
        }

        showAuthorCode(phone ,loginAction)
    }

    interface LoginStatusListen {
        fun login(type: Int, login: Boolean ,data :Any?)
        fun onDismiss()
    }

    fun  setLoginListen(listen :LoginStatusListen){
        if(statusListen !=null){
            statusListen =null
        }
        statusListen =listen;
    }

    fun unRegisterListen(){
        statusListen = null

    }


    fun showAuthorCode(phone: String,actionType :Int){
        if (authorCodeDialogFragment == null) {
            authorCodeDialogFragment = AuthorCodeDialogFragment(actionType)
        }else{
            authorCodeDialogFragment == null
            authorCodeDialogFragment = AuthorCodeDialogFragment(actionType)
        }

        authorCodeDialogFragment!!.setAuthorCodeListen(object : AuthorCodeDialogFragment.AuthorCodeListen {
            override fun getAuthorStatus(type: Int, login: Boolean,data : AuthorCodeModel?) {
                LogUtil.d(TAG , "getAuthorStatus type =" +type + ",login=" +login)
                if(login){
                    //验证码成功
                    if(data!=null){
                        loginModel.login(authorCodeLogin ,data.phone ,data.code ,"" ,"","")
                    }else{
                        LogUtil.d(TAG , "getAuthorStatus data null" )
                    }

                }else{
                    Toast.makeText(activity,"输入验证码失败", Toast.LENGTH_SHORT).show()
                }
            }

        })
       val bundle =Bundle()
        bundle.putString(PHONE_NUMBER ,phone)
        authorCodeDialogFragment!!.arguments=bundle
        val fm = activity!!.supportFragmentManager
        if (!authorCodeDialogFragment!!.isAdded && null == fm.findFragmentByTag("getAuthorCode")) {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(authorCodeDialogFragment!!, "getAuthorCode")
            ft.commitAllowingStateLoss()
        } else if (authorCodeDialogFragment!!.isHidden || !authorCodeDialogFragment!!.isVisible) {
            val ft = fm.beginTransaction()
            ft.show(authorCodeDialogFragment!!).commitNowAllowingStateLoss()
        } else {
            val ft = fm.beginTransaction()
            fm.executePendingTransactions()
            ft.add(authorCodeDialogFragment!!, "getAuthorCode")
            ft.commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginModel.onCleared()
        unRegisterListen()

    }

    fun showLoadingDialog(content: String) {
        customdialog?:let {
            customdialog = CommonLoadingDialog(mContext, content)
        }
        customdialog?.show()
    }

    fun dismissLoadingDialog() {
        customdialog?.dismiss()
    }
}