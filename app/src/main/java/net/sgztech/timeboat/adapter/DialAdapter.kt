package net.sgztech.timeboat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hjq.shape.view.ShapeView
import net.sgztech.timeboat.R
import net.sgztech.timeboat.bean.DialBean
import net.sgztech.timeboat.listeners.OnCommItemClickListener


/**
 * 表盘中心表盘的adapter
 */
  class DialAdapter(private val context: Context?, private val list: MutableList<DialBean>?) : RecyclerView.Adapter<DialAdapter.DialViewHolder>() {


    private var onItemClick : OnCommItemClickListener ?= null
    fun setOnItemClick(onclick : OnCommItemClickListener){
        this.onItemClick = onclick
    }

     class DialViewHolder(itemView: View) : ViewHolder(itemView){
        val imageView = itemView.findViewById<ImageView>(R.id.itemDialImgView)
        val checkView = itemView.findViewById<ShapeView>(R.id.itemDialCheckedView)
         val itemDialCheckedImgView = itemView.findViewById<ImageView>(R.id.itemDialCheckedImgView)
         val itemDialCheckView  = itemView.findViewById<View>(R.id.itemDialCheckView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.item_dial_img_view,parent,false)
        return DialViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialViewHolder, position: Int) {


        val dialBean = list?.get(position)

        if (dialBean != null) {
            if (context != null) {

                val roundOptions = RequestOptions().transform(RoundedCorners(35))
                //处理CenterCrop的情况,保证圆角不失效
                roundOptions.transform(CenterCrop(), RoundedCorners(35))


                val options = RequestOptions.bitmapTransform(RoundedCorners(25))
                Glide.with(context).load(dialBean.resourceId).apply(roundOptions).into(holder.imageView)
            }
        }

        if (dialBean != null) {
           // holder.itemDialCheckedImgView.visibility = if(dialBean.isChecked) View.VISIBLE else View.GONE
            holder.itemDialCheckView.visibility = if(dialBean.isChecked) View.VISIBLE else View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            val index = holder.layoutPosition
            onItemClick?.onItemClick(index)


        }
    }

    override fun getItemCount(): Int {
       return list?.size ?: 0
    }


}