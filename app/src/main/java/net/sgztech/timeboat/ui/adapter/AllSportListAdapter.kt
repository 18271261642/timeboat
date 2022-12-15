package net.sgztech.timeboat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.sgztech.timeboat.R
import net.sgztech.timeboat.bleCommand.CommandType
import net.sgztech.timeboat.managerUtlis.StringsUtils
import net.sgztech.timeboat.provide.dataModel.AllSportTypeDataInfoModel

class AllSportListAdapter (
    private val onClickListener: (AllSportTypeDataInfoModel, Int) -> Unit
) : RecyclerView.Adapter<AllSportListAdapter.ViewHolder>() {

    private val TAG =AllSportListAdapter::class.java.simpleName
    private lateinit  var content : Context
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sportIcon: ImageView = itemView.findViewById(R.id.sport_icon)
        val sportType: TextView = itemView.findViewById(R.id.sport_type)
        var useTime :TextView = itemView.findViewById(R.id.use_time)
        var useCount :TextView = itemView.findViewById(R.id.use_count)
        var distanceCount :TextView = itemView.findViewById(R.id.distance_count)
        val listItem : LinearLayout = itemView.findViewById(R.id.sport_item_view)

    }

    private val data = mutableListOf<AllSportTypeDataInfoModel>()

    fun setData(adInfoList: List<AllSportTypeDataInfoModel>) {
        data.addAll(adInfoList)
        notifyDataSetChanged()
    }

    fun refreshData(adInfoList: List<AllSportTypeDataInfoModel>) {
        data.clear()
        data.addAll(adInfoList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            when(sportType){
                CommandType.RUNNING.toInt() -> {
//                    holder.distanceCount.text  = "" +totalDistance +"米"
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalDistance.toDouble()/1000,2) +"千米"

               }
                CommandType.HIKING.toInt() -> {
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalDistance.toDouble()/1000,2) +"千米"

                }
                CommandType.RIDE.toInt()-> {
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalDistance.toDouble()/1000,2) +"千米"

                }
                CommandType.MOUNTAINEERING.toInt() -> {
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalDistance.toDouble()/1000,2) +"千米"
                }
                CommandType.RUNNING_MACHINE.toInt()-> {
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalCalorie.toDouble()/1000,2) +"千卡"

                }
                CommandType.BADMINTON.toInt() -> {
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalCalorie.toDouble()/1000,2) +"千卡"
                }
                CommandType.BASKETBALL.toInt()->  {
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalCalorie.toDouble()/1000,2) +"千卡"
                }
                CommandType.FOOTBALL.toInt()->{
                    holder.distanceCount.text  =  StringsUtils.getFormatDecimal(totalCalorie.toDouble()/1000,2) +"千卡"
                }
            }

            if(sportIcon!=null&&sportIcon.isNotEmpty()){
                Glide
                    .with(content)
                    .load(sportIcon)
                    .fitCenter()
                    .placeholder(R.mipmap.running_icon)
                    .into(holder.sportIcon)
            }

            holder.useTime.text = secondToHour(totalTime)
            holder.useCount.text = "" + count +"次"
            holder.sportType.text = sportName
            holder.itemView.setOnClickListener { onClickListener(this,position) }
        }
    }

    private fun secondToHour(totalSecond: Int): String {

        val second = totalSecond % 60
        val totalMinute = totalSecond / 60
        val minute = totalMinute % 60
        val hour = totalMinute / 60
        val time = StringBuffer()
        if (hour > 0) {
            val hourString = "" + hour + "小时"
            time.append(hourString)
        }
        if (minute > 0) {
            val minuteString = "" + minute + "分"
            time.append(minuteString)
        }

        val secondString = "" + second + "秒"
        time.append(secondString)

        return time.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        content = parent.context
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.sport_list_item, parent, false)
            .let { ViewHolder(it) }
    }
}
