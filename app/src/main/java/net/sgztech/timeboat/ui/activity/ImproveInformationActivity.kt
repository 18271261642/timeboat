package net.sgztech.timeboat.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.ActivityImproveInforBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.viewModel.ImproveInfoViewModel
import net.sgztech.timeboat.ui.popwindow.SelectBirthday
import net.sgztech.timeboat.ui.popwindow.SelectHeight
import net.sgztech.timeboat.ui.popwindow.SelectSex
import net.sgztech.timeboat.ui.popwindow.SelectWeight
import net.sgztech.timeboat.ui.utils.UIUtils
import java.util.*

class ImproveInformationActivity:BaseActivity(), PopupWindow.OnDismissListener {
    private var TAG =ImproveInformationActivity::class.java.simpleName
    private val improveInfoBinding :ActivityImproveInforBinding by viewBinding()
    private val improveInfoViewModel:ImproveInfoViewModel by viewModels()
    private var birthday = ""
    private var gender = 0
    private var height =""
    private var weight =""
    private var nickName ="" ;

    override fun getLayoutId(): Int {
        return  R.layout.activity_improve_infor
    }

    override fun initBindView() {
        improveInfoBinding.saveBtn.setOnClickListener(this)
        improveInfoBinding.heightLayout.setOnClickListener(this)
        improveInfoBinding.birthdayLayout.setOnClickListener(this)
        improveInfoBinding.weightLayout.setOnClickListener(this)
        improveInfoBinding.sexLayout.setOnClickListener(this)
        improveInfoBinding.nickNameIv.setOnClickListener(this)
        improveInfoBinding.nickNameLayout.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        var userModel = SettingInfoManager.instance.userModel
        if(userModel!=null &&userModel.avatarUrl!=null){
            if(userModel.nickname!=null &&userModel.nickname!!.isNotEmpty()){
                improveInfoBinding.nickNameIv.setText(userModel.nickname)
                nickName = userModel.nickname!!
            }

            if(userModel.avatarUrl!=null){
                Glide
                    .with(this@ImproveInformationActivity)
                    .load(userModel.avatarUrl)
                    .fitCenter()
                    .placeholder(R.mipmap.welecom_bg)
                    .into(improveInfoBinding.headerImg);
            }
            if(userModel.birthday!=null&& userModel.birthday!!.isNotEmpty()){
                improveInfoBinding.birthday.text = userModel.birthday
                birthday = userModel.birthday!!
            }


            gender =userModel.gender
            if(userModel.gender==1){
                improveInfoBinding.sex.text = "男"
            }else {
                improveInfoBinding.sex.text = "女"
            }

           if(userModel.height!=0){
               height = "" + userModel.height
               improveInfoBinding.height.text = "" +userModel.height
           }


        if(userModel.weight.isNotEmpty()){
            improveInfoBinding.weight.text = "" +userModel.weight
            weight = userModel.weight
        }


        }
    }

    override fun startObserve() {
        improveInfoViewModel.userInfoData.vmObserver(this){
            onAppLoading = {
                Log.d(TAG ,"开始")
            }
            onAppSuccess = {

                UToast.showShortToast("保存成功")
                finish()
            }
            onAppError = {msg,errorCode->
                if(errorCode == Constants.REPOSITORY_RESULT_TOKEN_INVALID){
                    launchOnUI {
                        cleanInfo()
                    }
                    val intent = Intent(this@ImproveInformationActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra(Constants.QUITE_CANCEL_ACCOUNT, Constants.QUITE_TO_HOME_PAGE)
                    startActivity(intent)
//                    exitOutAccount<MainActivity>()
                }
                UToast.showShortToast(msg)

            }
            onAppComplete = {
                Log.d(TAG ,"onAppComplete 完成")
            }
        }
    }

    private suspend fun cleanInfo() {
        SettingInfoManager.instance.cleanUserInfo()
        SettingInfoManager.instance.cleanBleModel()
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.height_layout->{
                UIUtils.hideSoftInput(this@ImproveInformationActivity, v)
                UIUtils.backgroundAlpha(this@ImproveInformationActivity, 0.5f)
                val heightPw = SelectHeight(this@ImproveInformationActivity)
                heightPw.setOnDismissListener(this)
                heightPw.showAtLocation(improveInfoBinding.llRoot, Gravity.BOTTOM, 0, 0)
                heightPw.setHeightId(height.toInt())
                heightPw.setHeightListener {
                        heightId, height -> improveInfoBinding.height.text = height
                        this.height =height
                }
                heightPw.update()
            }
            R.id.weight_layout->{
                UIUtils.hideSoftInput(this@ImproveInformationActivity, v)
                UIUtils.backgroundAlpha(this@ImproveInformationActivity, 0.5f)
                val weightPw = SelectWeight(this@ImproveInformationActivity)
                weightPw.setOnDismissListener(this)
                weightPw.showAtLocation(improveInfoBinding.llRoot, Gravity.BOTTOM, 0, 0)
                weightPw.setWeight(weight.toInt())
                weightPw.setWeightListener {
                        weightId, weight -> improveInfoBinding.weight.text = weight
                        this.weight =weight
                 }
                weightPw.update()
            }
            R.id.birthday_layout->{
                UIUtils.hideSoftInput(this@ImproveInformationActivity, v)
                UIUtils.backgroundAlpha(this@ImproveInformationActivity, 0.5f)
                val birth = SelectBirthday(this@ImproveInformationActivity)
                birth.setOnDismissListener(this)
                birth.showAtLocation(improveInfoBinding.llRoot, Gravity.BOTTOM, 0, 0)
                val date = DateUtil.getDateFromString(birthday)
                val cal = Calendar.getInstance()
                cal.time = date
                val yearNow = cal[Calendar.YEAR]
                val monthNow = cal[Calendar.MONTH] + 1
                val dayOfMonthNow = cal[Calendar.DAY_OF_MONTH]

                birth.setDate(yearNow, monthNow, dayOfMonthNow)
                birth.setBirthdayListener { year, month, day ->
                    val birthday = "$year-$month-$day"
                    val dbirthday: Date = DateUtil.getDateFromString(birthday)
                    val age: Int = DateUtil.getAgeByBirthday(dbirthday)
                    improveInfoBinding.birthday.text = birthday
                    this.birthday = birthday
                }
                birth.update()
            }
            R.id.sex_layout->{
                UIUtils.hideSoftInput(this@ImproveInformationActivity, v)
                UIUtils.backgroundAlpha(this@ImproveInformationActivity, 0.5f)
                val sexPw = SelectSex(this@ImproveInformationActivity)
                sexPw.setOnDismissListener(this)
                sexPw.showAtLocation(improveInfoBinding.llRoot, Gravity.BOTTOM, 0, 0)
                sexPw.setSexListener { sexId, sex -> improveInfoBinding.sex.text = sex
                        this.gender = sexId
                }
                sexPw.update()
            }
            R.id.save_btn ->{
                improveInfoViewModel.improveInfoDate("" ,birthday,gender,height,improveInfoBinding.nickNameIv.text.toString().trim() ,weight)
             }
            R.id.nick_name_iv,
            R.id.nick_name_layout ->{

                var size = improveInfoBinding.nickNameIv.text.toString().trim().length
                improveInfoBinding.nickNameIv.setSelection(size)
                improveInfoBinding.nickNameIv.isFocusable = true
                improveInfoBinding.nickNameIv.isFocusableInTouchMode = true
                improveInfoBinding.nickNameIv.requestFocus()
                UIUtils.showSoftInput(this, improveInfoBinding.nickNameIv)
            }
        }
    }



   override fun onDismiss() {
        // TODO Auto-generated method stub
        UIUtils.backgroundAlpha(this@ImproveInformationActivity, 1f)
    }

}