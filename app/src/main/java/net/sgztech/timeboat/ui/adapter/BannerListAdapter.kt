package net.sgztech.timeboat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.sgztech.timeboat.R
import net.sgztech.timeboat.provide.dataModel.AdInfoModel

class BannerListAdapter (
    private val onClickListener: (AdInfoModel, Int) -> Unit
) : RecyclerView.Adapter<BannerListAdapter.ViewHolder>() {

    private val TAG =BannerListAdapter::class.java.simpleName
    private lateinit  var content :Context
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adIv: ImageView = itemView.findViewById(R.id.ad_iv)
        val listItem : LinearLayout = itemView.findViewById(R.id.banner_item_view)
    }

    private val data = mutableListOf<AdInfoModel>()

    fun setData(adInfoList: List<AdInfoModel>) {
        data.addAll(adInfoList)
        notifyDataSetChanged()
    }

    fun refreshData(adInfoList: List<AdInfoModel>) {
        data.clear()
        data.addAll(adInfoList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            if(adType ==1){
                Glide
                    .with(content)
                    .load(adUrl)
                    .fitCenter()
                    .placeholder(R.mipmap.welecom_bg)
                    .into(holder.adIv)
            }

            holder.adIv.setOnClickListener {

            }
            holder.itemView.setOnClickListener { onClickListener(this,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        content = parent.context
       return LayoutInflater.from(parent.context)
            .inflate(R.layout.banner_list_item, parent, false)
            .let { ViewHolder(it) }
    }
}
