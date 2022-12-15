package net.sgztech.timeboat.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.device.ui.widget.SplitEditText
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.AuthorCodeFragmentBinding
import net.sgztech.timeboat.provide.viewModel.AuthorCodeViewModel
import net.sgztech.timeboat.provide.dataModel.AuthorCodeModel
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.bindingPhoneAction
import net.sgztech.timeboat.ui.activity.MainActivity.Companion.loginAction

class AuthorCodeDialogFragment(type :Int):DialogFragment(),View.OnClickListener{

    private val  TAG =AuthorCodeDialogFragment::class.java.simpleName
    private  val authorCodeBinding : AuthorCodeFragmentBinding by viewBinding()

    private val authorViewModel :AuthorCodeViewModel by viewModels()
    private var phone:String? = null
    private var authorListen : AuthorCodeListen? =null
    private val countDownInterval: Long = 1000
    private var timeCount: TimeCount? = null
    private var actionType : Int=0
    private var authorCode: AuthorCodeModel?=null

    init{
        actionType =type
    }

    companion object {
        //成功输入验证码登录
       const val codeLoginType =1
        //按键返回
       const val backArrowType =2
        //超时返回
       const val timeOutTypeType =3
        //绑定手机
       const val bindPhoneType= 4
    }

    fun updateArgs(type :Int){
        actionType = type
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialogAnimation)
        this.isCancelable = false
        phone = arguments?.getString(Constants.PHONE_NUMBER)
        getTimCount(60*1000)
    }

    fun getTimCount(totalTime: Long){
        if (timeCount == null) {
            timeCount = TimeCount(totalTime, countDownInterval)
        }
        timeCount!!.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.author_code_fragment, null)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authorCodeBinding.authorCodeGet.setOnTextInputListener(object : SplitEditText.OnSimpleTextInputListener() {

            override fun onTextInputCompleted(text: String) {
                if(authorCode!=null&&authorCode!!.code.equals(text)){

                    if(authorListen!=null){
                        val authorModel = AuthorCodeModel()
                        authorModel.phone =phone
                        authorModel.code=text
                        if(actionType==loginAction){
                            authorListen!!.getAuthorStatus(codeLoginType,true,authorModel)
                        }else if(actionType == bindingPhoneAction){
                            authorListen!!.getAuthorStatus(bindPhoneType,true,authorModel)
                        }else{
                            LogUtil.d(TAG , "类型错误" )
                        }
                        dismissAllowingStateLoss()
                    }
                }else{

                    UToast.showShortToast("输入验证码错误 text=" +text)

                }
            }
        })

        authorCodeBinding.backArrow.setOnClickListener(this)

        authorViewModel.authorCodeData.vmObserver(this){
            onAppLoading = {
                LogUtil.d(TAG ,"send start")
            }
            onAppSuccess = {
                authorCode =it
                LogUtil.d(TAG , "send success it=" + it?.code )

            }
            onAppError = {
                    msg, _ ->
                UToast.showShortToast(msg)
                LogUtil.d(TAG , "loginWxData error =" + msg)

            }
            onAppComplete = {
                Log.d(TAG ,"onAppComplete")
            }
        }

        if(phone!=null){
            authorCodeBinding.sendPhone.text=phone
            authorViewModel.authorCode(phone!!,actionType)
        }else{
            Log.d(TAG ,"phone null")
        }

    }


    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.back_arrow ->{
                if(authorListen!=null){
                    authorListen!!.getAuthorStatus(backArrowType,false,null)
                }
                dismissAllowingStateLoss()
            }
        }
    }

    interface AuthorCodeListen{
        fun getAuthorStatus(type: Int, login: Boolean ,data : AuthorCodeModel?)
    }

    fun  setAuthorCodeListen(listen : AuthorCodeListen){
        authorListen =listen;
    }


   inner class TimeCount(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            timeUpdate(millisUntilFinished)
        }

        override fun onFinish() {
            onComplete()
        }
    }

    fun  timeUpdate(millisUntilFinished: Long){
        authorCodeBinding.timeCount.text = "" + millisUntilFinished / 1000
    }

    fun onComplete(){
        if(authorListen!=null){
            authorListen!!.getAuthorStatus(timeOutTypeType,false,null)
        }
        dismissAllowingStateLoss()
    }

    private fun stopTimeCount() {
        if (timeCount != null) {
            timeCount!!.cancel()
            timeCount = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimeCount();
    }
}