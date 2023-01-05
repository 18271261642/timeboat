package net.sgztech.timeboat.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import net.sgztech.timeboat.R


class DeviceSetDialView : LinearLayout {

    private var img1View : ImageView ?= null

    private var img2View : ImageView ?= null

    private var img3View : ImageView ?= null


    constructor(context: Context) : super (context){

    }


    constructor(context: Context, attribute : AttributeSet) : super (context,attribute){
        initViews(context)
    }

    constructor(context: Context, attribute: AttributeSet, defStyleAttr : Int) : super (context,attribute,defStyleAttr){
        initViews(context)
    }



    private fun initViews(context: Context){
        val view = LayoutInflater.from(context).inflate(R.layout.item_device_set_dial_layout,this,true)
        img1View = view.findViewById(R.id.img1View)
        img2View = view.findViewById(R.id.img2View)
        img3View = view.findViewById(R.id.img3View)

        setImgs(context)
    }


    private fun setImgs(context: Context){
        //圆
       // val options = RequestOptions.circleCropTransform()

        val options = RequestOptions.bitmapTransform(RoundedCorners(25))

        img1View?.let { Glide.with(context).load(R.drawable.dial_10_bg).apply(options).into(it) }
        img2View?.let { Glide.with(context).load(R.drawable.dial_12_bg).apply(options).into(it) }
        img3View?.let { Glide.with(context).load(R.drawable.dial_13_bg).apply(options).into(it) }
    }


}