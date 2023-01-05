package net.sgztech.timeboat.ui.activity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.device.ui.baseUi.baseActivity.BaseActivity
import net.sgztech.timeboat.R
import net.sgztech.timeboat.adapter.DialAdapter
import net.sgztech.timeboat.bean.DialBean
import net.sgztech.timeboat.listeners.OnCommItemClickListener

/**
 * 表盘市场页面
 */
class DialActivity : BaseActivity() {

    //预览的图片
    private var dialPreviewImgView : ImageView ?= null
    //rv
    private var dialRecyclerView : RecyclerView ?= null

    //title
    private var title_name : TextView ?= null
    private var save_user_info : TextView ?= null
    private var back_arrow : ImageView ?= null

    private var list : MutableList<DialBean> ?= null
    private var adapter : DialAdapter ?= null



    override fun getLayoutId(): Int {
       return R.layout.activity_dial_layout
    }

    override fun initBindView() {
        title_name = findViewById(R.id.title_name)
        save_user_info = findViewById(R.id.save_user_info)
        back_arrow = findViewById(R.id.back_arrow)
        dialPreviewImgView = findViewById(R.id.dialPreviewImgView)
        dialRecyclerView = findViewById(R.id.dialRecyclerView)
        val gridLayoutManager = GridLayoutManager(this,3)
        dialRecyclerView?.layoutManager = gridLayoutManager

        list = mutableListOf()
        adapter = DialAdapter(this@DialActivity,list)
        dialRecyclerView?.adapter = adapter

        adapter?.setOnItemClick(onItemClick)

        initImgs()

        title_name?.text = "表盘中心"
        save_user_info?.visibility = View.VISIBLE
        back_arrow?.setOnClickListener { finish() }

        val options = RequestOptions.bitmapTransform(RoundedCorners(45))
        dialPreviewImgView?.let {
            Glide.with(this@DialActivity).load(R.drawable.dial_10_bg).apply(options).into(
                it
            )
        }
    }


    private val onItemClick : OnCommItemClickListener =
        OnCommItemClickListener { position ->
            val resourceId = list?.get(position)?.resourceId

            val options = RequestOptions.bitmapTransform(RoundedCorners(45))
            dialPreviewImgView?.let {
                Glide.with(this@DialActivity).load(resourceId).apply(options).into(
                    it
                )
            }
        }

    private fun initImgs(){
        val imgList = arrayListOf<Int>(R.drawable.dial_10_bg,R.drawable.dial_12_bg,R.drawable.dial_13_bg,R.drawable.dial_14_bg,R.drawable.dial_15_bg,R.drawable.dial_16_bg,
        R.drawable.dial_17_bg,R.drawable.dial_18_bg,R.drawable.dial_19_bg)
        list?.clear()
        imgList.forEach {
            val bean = DialBean(it,false)
            list?.add(bean)
        }


        adapter?.notifyDataSetChanged()
    }
}