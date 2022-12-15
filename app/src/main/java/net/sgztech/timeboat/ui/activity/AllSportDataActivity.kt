package net.sgztech.timeboat.ui.activity

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import net.sgztech.timeboat.R
import net.sgztech.timeboat.databinding.AllSportDataActivityBinding
import net.sgztech.timeboat.provide.dataModel.BottomResourceData
import net.sgztech.timeboat.ui.adapter.WalkDataPageAdapter
import net.sgztech.timeboat.ui.fragment.*
import net.sgztech.timeboat.ui.utils.UIUtils

class AllSportDataActivity :BaseActivity(){

    private val TAG =AllSportDataActivity::class.java.simpleName
    private val sportBinding : AllSportDataActivityBinding by viewBinding()
    override fun getLayoutId(): Int {
       return R.layout.all_sport_data_activity
    }

    override fun initBindView() {
        sportBinding.sportTitleBar.titleName.text="所有运动"
        sportBinding.sportTitleBar.backArrow.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        var bottomList = ArrayList<BottomResourceData>()
        bottomList.add(BottomResourceData("周", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("月", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("年", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        val mFragmentList = ArrayList<BaseFragment>()
        AllSportWeekFragment.newInstance("sportWeek").let { mFragmentList.add(it) }
        AllSportMonthFragment.newInstance("sportMonth").let { mFragmentList.add(it) }
        AllSportYearFragment.newInstance("sportYear").let { mFragmentList.add(it) }
        sportBinding.allSportContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

        sportBinding.allSportContent.adapter =
            WalkDataPageAdapter(this@AllSportDataActivity,mFragmentList)
        sportBinding.allSportContent.offscreenPageLimit = mFragmentList.size - 1
        TabLayoutMediator(sportBinding.sportTabLayout,sportBinding.allSportContent,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                {

                }
            }).attach()

        UIUtils.setHomeTablayoutCustomView(this@AllSportDataActivity, sportBinding.sportTabLayout, bottomList)

    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.back_arrow ->{
                finish()
            }
        }
    }

}