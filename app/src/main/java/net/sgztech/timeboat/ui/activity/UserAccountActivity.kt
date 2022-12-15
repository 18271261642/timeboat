package net.sgztech.timeboat.ui.activity


import android.app.AlertDialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.*

import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.imlaidian.utilslibrary.config.IntentConstant
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.LogUtil
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants.Companion.QUITE_CANCEL_ACCOUNT
import net.sgztech.timeboat.config.Constants.Companion.QUITE_TO_HOME_PAGE
import net.sgztech.timeboat.databinding.ActivityUserAccoutBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.viewModel.ImproveInfoViewModel
import net.sgztech.timeboat.ui.popwindow.SelectBirthday
import net.sgztech.timeboat.ui.popwindow.SelectHeight
import net.sgztech.timeboat.ui.popwindow.SelectSex
import net.sgztech.timeboat.ui.popwindow.SelectWeight
import net.sgztech.timeboat.ui.utils.UIUtils
import java.util.*

class UserAccountActivity : BaseActivity(), PopupWindow.OnDismissListener {
    private val TAG =UserAccountActivity::class.java.simpleName
    private val userBinding: ActivityUserAccoutBinding by viewBinding()
    private val improveInfoViewModel: ImproveInfoViewModel by viewModels()
    private var birthday = ""
    private var gender = 0
    private var height = ""
    private var weight = ""
    private var nickName =""
    override fun getLayoutId(): Int {
        return R.layout.activity_user_accout
    }

    override fun initBindView() {
        userBinding.exitBtn.setOnClickListener(this)
        userBinding.accountTitleBar.backArrow.setOnClickListener(this)
        userBinding.accountTitleBar.titleName.text = "账号与资料"
        userBinding.accountTitleBar.saveUserInfo.setOnClickListener(this)
        userBinding.heightLayout.setOnClickListener(this)
        userBinding.birthdayLayout.setOnClickListener(this)
        userBinding.weightLayout.setOnClickListener(this)
        userBinding.sexLayout.setOnClickListener(this)
        userBinding.accountTitleBar.saveUserInfo.isVisible =true
    }


    override fun initData() {
        super.initData()
        var userModel = SettingInfoManager.instance.userModel
        if (userModel != null && userModel.avatarUrl != null) {
            if (userModel.nickname != null && userModel.nickname!!.isNotEmpty()) {
                userBinding.nickNameIv.setText(userModel.nickname)

                nickName = userModel.nickname!!
            }

            if (userModel.avatarUrl != null) {
                Glide
                    .with(this@UserAccountActivity)
                    .load(userModel.avatarUrl)
                    .fitCenter()
                    .placeholder(R.mipmap.welecom_bg)
                    .into(userBinding.headerImg);
            }

            if (userModel.birthday != null && userModel.birthday!!.isNotEmpty()) {
                userBinding.birthday.text = userModel.birthday
                birthday = userModel.birthday!!
            }

            gender = userModel.gender
            if (userModel.gender == 1) {
                userBinding.sex.text = "男"
            } else {
                userBinding.sex.text = "女"
            }

            height = "" + userModel.height
            if (userModel.height != 0) {
                userBinding.height.text = "" + userModel.height
            }

            weight = userModel.weight
            if (userModel.weight.isNotEmpty()) {
                userBinding.weight.text = "" + userModel.weight
            }
            userBinding.phoneIv.setText(userModel.phone)
            userBinding.nickNameIv.addTextChangedListener(object:
                TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    userBinding.accountTitleBar.saveUserInfo.isVisible =true
                }

                override fun afterTextChanged(text: Editable?) {
                    if(text!=null){
                        nickName =text.toString()
                    }

                }
            })
        }
    }


    override fun startObserve() {
        improveInfoViewModel.userInfoData.vmObserver(this){
            onAppLoading = {
                LogUtil.d(TAG ,"开始")
            }
            onAppSuccess = {

                UToast.showShortToast("保存成功")
                finish()
            }
            onAppError = { msg ,errorCode->

                UToast.showShortToast("msg")
                LogUtil.d(TAG , "error it=" + msg)
                exitOutAccount<MainActivity>()

            }
            onAppComplete = {
                LogUtil.d(TAG ,"onAppComplete 完成")
            }
        }
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when (v.id) {
            R.id.height_layout -> {
                UIUtils.hideSoftInput(this@UserAccountActivity, v)
                UIUtils.backgroundAlpha(this@UserAccountActivity, 0.5f)
                val heightPw = SelectHeight(this@UserAccountActivity)
                heightPw.setOnDismissListener(this)
                heightPw.showAtLocation(userBinding.llRoot, Gravity.BOTTOM, 0, 0)
                heightPw.setHeightId(height.toInt())
                heightPw.setHeightListener { heightId, height ->
                    userBinding.height.text = height
                    this.height = height
                    userBinding.accountTitleBar.saveUserInfo.isVisible =true
                }
                heightPw.update()
            }
            R.id.weight_layout -> {
                UIUtils.hideSoftInput(this@UserAccountActivity, v)
                UIUtils.backgroundAlpha(this@UserAccountActivity, 0.5f)
                val weightPw = SelectWeight(this@UserAccountActivity)
                weightPw.setOnDismissListener(this)
                weightPw.showAtLocation(userBinding.llRoot, Gravity.BOTTOM, 0, 0)
                weightPw.setWeight(weight.toInt())
                weightPw.setWeightListener { weightId, weight ->
                    userBinding.weight.text = weight
                    this.weight = weight
                    userBinding.accountTitleBar.saveUserInfo.isVisible =true
                }
                weightPw.update()
            }
            R.id.birthday_layout -> {
                UIUtils.hideSoftInput(this@UserAccountActivity, v)
                UIUtils.backgroundAlpha(this@UserAccountActivity, 0.5f)
                val birth = SelectBirthday(this@UserAccountActivity)
                birth.setOnDismissListener(this)
                birth.showAtLocation(userBinding.llRoot, Gravity.BOTTOM, 0, 0)
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
                    userBinding.birthday.text = birthday
                    this.birthday = birthday
                    userBinding.accountTitleBar.saveUserInfo.isVisible =true
                }
                birth.update()
            }
            R.id.sex_layout -> {
                UIUtils.hideSoftInput(this@UserAccountActivity, v)
                UIUtils.backgroundAlpha(this@UserAccountActivity, 0.5f)
                val sexPw = SelectSex(this@UserAccountActivity)
                sexPw.setOnDismissListener(this)
                sexPw.showAtLocation(userBinding.llRoot, Gravity.BOTTOM, 0, 0)
                sexPw.setSexListener { sexId, sex ->
                    userBinding.sex.text = sex
                    this.gender = sexId
                    userBinding.accountTitleBar.saveUserInfo.isVisible =true
                }
                sexPw.update()
            }
            R.id.save_user_info -> {
                improveInfoViewModel.improveInfoDate("", birthday, gender, height, userBinding.nickNameIv.text.toString().trim(), weight)
            }

            R.id.exit_btn -> {
//                 exitOutAccount<MainActivity>()
                checkQuiteAppDialog()

            }
            R.id.back_arrow -> {
               finish()
            }

        }
    }

    private fun checkQuiteAppDialog(){

        AlertDialog.Builder(this@UserAccountActivity).apply {
            setMessage("确定退出登录?")
            setPositiveButton("确定") { dialog, which ->
                dialog.dismiss()
                quiteLogin()

            }
            setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            create()
        }.show()

    }

    private fun quiteLogin(){
        launchOnUI {
            cleanInfo()
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(QUITE_CANCEL_ACCOUNT, QUITE_TO_HOME_PAGE)
        startActivity(intent)
    }

    private suspend fun cleanInfo() {
        SettingInfoManager.instance.cleanUserInfo()
        SettingInfoManager.instance.cleanBleModel()
    }

    override fun onDismiss() {
        // TODO Auto-generated method stub
        UIUtils.backgroundAlpha(this@UserAccountActivity, 1f)
    }
}