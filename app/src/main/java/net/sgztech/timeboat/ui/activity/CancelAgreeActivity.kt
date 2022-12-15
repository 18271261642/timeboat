package net.sgztech.timeboat.ui.activity

import android.content.Intent
import android.view.View
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import net.sgztech.timeboat.R
import net.sgztech.timeboat.databinding.CancelAgreeActivityBinding

class CancelAgreeActivity:BaseActivity() {
    private  val cancelBinding :CancelAgreeActivityBinding by viewBinding()

    override fun getLayoutId(): Int {
        return R.layout.cancel_agree_activity

    }

    override fun initBindView() {
        cancelBinding.cancelTitleBar.backArrow.setOnClickListener(this)
        cancelBinding.cancelTitleBar.titleName.text="注销账号"
        cancelBinding.cancelAgreeBtn.setOnClickListener(this)
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        if(v.id ==R.id.cancel_agree_btn){
            val intent = Intent(this,CancelAgreeSureActivity::class.java)
            startActivity(intent)
        }else if(v.id ==R.id.back_arrow){
            finish()
        }
    }
}