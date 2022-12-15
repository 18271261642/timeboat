package net.sgztech.timeboat.ui.fragment

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
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.FragmentBindingPhoneBinding
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.provide.viewModel.BindingPhoneViewModel
import net.sgztech.timeboat.ui.activity.ImproveInformationActivity
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.bindingPhoneAction
import net.sgztech.timeboat.ui.fragment.LoginDialogFragment.Companion.closeBinding

class BindingPhoneDialogFragment : DialogFragment(), View.OnClickListener {
    private val TAG =BindingPhoneDialogFragment::class.java.simpleName
    private  val phoneBinding : FragmentBindingPhoneBinding by viewBinding()
    private var statusListen :BindStatusListen? =null
    private var authorCodeDialogFragment :AuthorCodeDialogFragment ? =null
    private val bindingPhoneViewModel : BindingPhoneViewModel by viewModels()
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
        val mView: View = inflater.inflate(R.layout.fragment_binding_phone, null)

        return mView
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneBinding.loginCheckCodeDo.setOnClickListener(this)
        phoneBinding.closeDialog.setOnClickListener(this)
        phoneBinding.loginPhoneNum.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!=null&&p0.length>=11){
                    phoneBinding.loginCheckCodeDo.isEnabled =true
                }
            }

        })

        bindingPhoneViewModel.userLoginData.vmObserver(this){
            onAppLoading = {
                Log.d(TAG ,"开始")
            }
            onAppSuccess = {
                if(it!=null&&it.phone!=null && it.phone!!.isNotEmpty() && it.isNewUser==1){
                    if(statusListen!=null){
                        statusListen!!.login(LoginDialogFragment.improveInformationAction,true,it)
                        statusListen!!.onDismiss()
                        dismissAllowingStateLoss()
                    }
                }else if(it!=null&&it.phone!=null && it.phone!!.isNotEmpty()&&it.isNewUser==0){

                    if(statusListen!=null){
                        statusListen!!.login(LoginDialogFragment.authorLoginSuccessAction,true,it)
                        statusListen!!.onDismiss()
                        dismissAllowingStateLoss()
                    }
                    UToast.showShortToast("绑定成功")
                }else if(it!=null&&it.phone==null ||
                    (it!=null&&it.phone!=null &&it.phone!!.isEmpty())){
                    //微信无手机号，执行绑定操作
                    if(statusListen!=null){
                        statusListen!!.login(LoginDialogFragment.bindPhoneAction,true,it)
                        statusListen!!.onDismiss()
                        dismissAllowingStateLoss()
                    }
                }
            }
            onAppError = {msg,errorCode->
                UToast.showShortToast("绑定失败:"+msg)
                LogUtil.d(TAG , "error it=" + msg)

            }
            onAppComplete = {
                Log.d(TAG ,"onAppComplete 完成")
            }
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){

            R.id.login_check_code_do->{
                checkCodeClick()
            }
            R.id.close_dialog -> {
                if(statusListen!=null){
                    statusListen!!.login(closeBinding,false,null)
                    statusListen!!.onDismiss()

                }
                dismissAllowingStateLoss()
            }
        }
    }

    fun checkCodeClick() {
        val phone =phoneBinding.loginPhoneNum.text.toString();
        if (phone.isEmpty()) {
            Toast.makeText(activity,"请输入电话号码", Toast.LENGTH_SHORT).show()
            return
        }

        showAuthorCode(phone )

    }

    interface BindStatusListen {
        fun login(type: Int, login: Boolean ,data :Any?)
        fun onDismiss()
    }

    fun  setBindListen(listen :BindStatusListen){
        if(statusListen !=null){
            statusListen =null
        }
        statusListen =listen;
    }

    fun unRegisterListen(){
        statusListen = null
    }


    fun showAuthorCode(phone: String){
        if (authorCodeDialogFragment == null) {
            authorCodeDialogFragment = AuthorCodeDialogFragment(bindingPhoneAction)
        }else{
            authorCodeDialogFragment = null
            authorCodeDialogFragment = AuthorCodeDialogFragment(bindingPhoneAction)
        }

        authorCodeDialogFragment!!.setAuthorCodeListen(object : AuthorCodeDialogFragment.AuthorCodeListen {
            override fun getAuthorStatus(type: Int, login: Boolean,data : AuthorCodeModel?) {
                LogUtil.d(TAG , "getAuthorStatus type =" +type + ",login=" +login)
                if(login){
                    //验证码成功
                    if(data!=null){
                        bindingPhoneViewModel.bindingPhone(data.phone!! ,data.code!!)
                    }
                }else{
                    Toast.makeText(activity,"输入验证码失败", Toast.LENGTH_SHORT).show()
                }
            }

        })
        val bundle = Bundle()
        bundle.putString(Constants.PHONE_NUMBER,phone)
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
        unRegisterListen()
        bindingPhoneViewModel.onCleared()
    }

}