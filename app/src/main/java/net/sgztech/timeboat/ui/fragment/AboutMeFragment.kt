package net.sgztech.timeboat.ui.fragment


import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.FragmentAboutMeBinding
import net.sgztech.timeboat.managerUtlis.SettingInfoManager
import net.sgztech.timeboat.provide.dataModel.BleConnectEvent
import net.sgztech.timeboat.provide.dataModel.LoginStatusEvent
import net.sgztech.timeboat.ui.activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AboutMeFragment : BaseFragment() {
    private val  TAG = AboutMeFragment::class.java.simpleName
    private val aboutMeBinding: FragmentAboutMeBinding by viewBinding()

    fun newInstance(): AboutMeFragment {
        val fragment: AboutMeFragment =
            AboutMeFragment()
        fragment.setLabel(Constants.FRAGMENT_LABEL_ABOUT_ME)
        fragment.setMainPage(false)
        return fragment
    }


    override fun getLayoutResId(): Int {
        return  R.layout.fragment_about_me
    }

    override fun initBindView() {
        EventBus.getDefault().register(this)
        aboutMeBinding.accountLayout.setOnClickListener(this)
        aboutMeBinding.settingLayout.setOnClickListener(this)
        aboutMeBinding.shareLayout.setOnClickListener(this)
        aboutMeBinding.aboutLayout.setOnClickListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtil.d(TAG, "onHiddenChanged hide =" +hidden)
        if (!hidden) {
            updateUi()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    override fun initData() {
        updateUi();

    }

    private fun updateUi(){
        var userModel = SettingInfoManager.instance.userModel
        if(userModel!=null ){
            if(userModel.nickname!=null&&userModel.nickname!!.isNotEmpty()){
                aboutMeBinding.nickName.text = userModel.nickname
            }

            if(userModel.avatarUrl!=null){
                Glide
                    .with(this)
                    .load(userModel.avatarUrl)
                    .fitCenter()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(aboutMeBinding.headerImg);
            }
        }else{
            aboutMeBinding.nickName.text = ""
            aboutMeBinding.headerImg.setImageResource(R.mipmap.ic_launcher)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LoginStatusEvent) {
        var status = event.loginStatus
        updateUi();
    }


    override fun widgetClick(v: View) {
        super.widgetClick(v)
        if (SettingInfoManager.instance.isAccessTokenNotEmpty()) {
            if (SettingInfoManager.instance.isPhoneEmpty()) {
                (activity as MainActivity).showBinding()
            } else {
                onClickEvent(v)
            }
        } else {
            (activity as MainActivity).showLogin()
        }
    }

    fun onClickEvent(v: View) {
        when(v.id){
            R.id.account_layout->{
                var intent = Intent(activity, UserAccountActivity::class.java)
                activity!!.startActivity(intent)
            }
            R.id.setting_layout->{
                val intent = Intent(activity,SettingActivity::class.java)
                activity!!.startActivity(intent)
            }
            R.id.share_layout->{

            }
            R.id.about_layout->{
                val intent = Intent(activity,AboutActivity::class.java)
                activity!!.startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this);
    }
}