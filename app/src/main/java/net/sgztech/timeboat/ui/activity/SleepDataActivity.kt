package net.sgztech.timeboat.ui.activity

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import net.sgztech.timeboat.R
import net.sgztech.timeboat.databinding.ActivityHeartRateDataBinding
import net.sgztech.timeboat.databinding.ActivitySleepDataBinding
import net.sgztech.timeboat.provide.dataModel.BottomResourceData
import net.sgztech.timeboat.ui.adapter.WalkDataPageAdapter
import net.sgztech.timeboat.ui.fragment.*
import net.sgztech.timeboat.ui.utils.UIUtils

class SleepDataActivity :BaseActivity() {
    private val sleepBinding: ActivitySleepDataBinding by viewBinding()

    override fun getLayoutId(): Int {
        return R.layout.activity_sleep_data
    }

    override fun initBindView() {
        sleepBinding.sleepTitleBar.titleName.text="睡眠"
        sleepBinding.sleepTitleBar.backArrow.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        val bottomList = ArrayList<BottomResourceData>()
        bottomList.add(BottomResourceData("日", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("周", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("月", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("年", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        val mFragmentList = ArrayList<BaseFragment>()
        SleepDayFragment.newInstance("sleepDay" ).let { mFragmentList.add(it) }
        SleepWeekFragment.newInstance("sleepWeek").let { mFragmentList.add(it) }
        SleepMonthFragment.newInstance("sleepMonth").let { mFragmentList.add(it) }
        SleepYearFragment.newInstance("sleepYear").let { mFragmentList.add(it) }
        sleepBinding.sleepContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
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

        sleepBinding.sleepContent.adapter =
            WalkDataPageAdapter(this@SleepDataActivity,mFragmentList)
        sleepBinding.sleepContent.offscreenPageLimit = mFragmentList.size - 1
        TabLayoutMediator(sleepBinding.sleepTabLayout,sleepBinding.sleepContent
        ) { tab, position ->
            {

            }
        }.attach()

        UIUtils.setHomeTablayoutCustomView(this@SleepDataActivity, sleepBinding.sleepTabLayout, bottomList)

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