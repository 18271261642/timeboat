package net.sgztech.timeboat.ui.activity

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.device.ui.baseUi.baseActivity.BaseActivity
import net.sgztech.timeboat.R
import net.sgztech.timeboat.ui.dialog.TimeSelectorDialog
import kotlin.math.min

/**
 * Created by Admin
 *Date 2023/2/22
 */
class CommRemindActivity : BaseActivity() {

    //title
    private var title_name : TextView?= null
    //返回
    private var back_arrow : ImageView?= null
    private var save_user_info : TextView ?= null
    //开始时间
    private var comRemindStartTimeTv : TextView ?= null
    //结束时间
    private var comRemindEndTimeTv : TextView ?= null


    private var commRemindStartLayout : ConstraintLayout ?= null
    private var commRemindEndLayout : ConstraintLayout ?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_comm_remind_layout
    }

    override fun initBindView() {
        back_arrow = findViewById(R.id.back_arrow)
        title_name = findViewById(R.id.title_name)
        save_user_info = findViewById(R.id.save_user_info)
        commRemindStartLayout = findViewById(R.id.commRemindStartLayout)
        commRemindEndLayout = findViewById(R.id.commRemindEndLayout)
        comRemindStartTimeTv = findViewById(R.id.comRemindStartTimeTv)
        comRemindEndTimeTv = findViewById(R.id.comRemindEndTimeTv)

    }


    override fun initData() {
        super.initData()

        save_user_info?.visibility = View.VISIBLE
        save_user_info?.text = "保存"
        back_arrow?.setOnClickListener {
            finish()
        }



        //保存
        save_user_info?.setOnClickListener {
            finish()
        }

        //开始时间
        commRemindStartLayout?.setOnClickListener {
            showDialogSelector(0)
        }

        //结束时间
        commRemindEndLayout?.setOnClickListener {
            showDialogSelector(1)
        }


        val code = intent.getIntExtra("code",-1)
        title_name?.text = showTitle(code)

    }


    //显示时间选择
    private fun showDialogSelector(code : Int){
        val dialog = TimeSelectorDialog(this,R.style.edit_AlertDialog_style)
        dialog.show()
        dialog.setOnListener(object : TimeSelectorDialog.SignalSelectListener{
            override fun onSignalSelect(hour: String, minute: String) {
               val time = "$hour:$minute"

                if(code == 0){
                    comRemindStartTimeTv?.text = time
                }else{
                    comRemindEndTimeTv?.text = time
                }
            }

        })

        val window = dialog.window
        val attributeSet = window?.attributes
        attributeSet?.gravity = Gravity.BOTTOM
        window?.attributes = attributeSet

    }


    //根据标识显示标题栏
    private fun showTitle(code : Int) : String{
        if(code == 0x00){   //久坐
            return "久坐提醒"
        }
        if(code == 0x01){
            return "喝水提醒"
        }
        if(code == 0x02){
            return "勿扰模式"
        }
        return "久坐提醒"
    }
}