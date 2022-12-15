package net.sgztech.timeboat.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.sgztech.timeboat.config.Constants.Companion.FRAGMENT_ARG_OBJECT

class WalkDataPageAdapter(activity: FragmentActivity,val fragmentList:List<Fragment>) : FragmentStateAdapter(activity) {//fragment 也可以换为 activity

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
//        val fragment =  fragmentList[position]
//        fragment.arguments = Bundle().apply {
//            putInt(FRAGMENT_ARG_OBJECT,position+1)
//        }
        return fragmentList[position]
    }//返回需要创建的fragment

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}


