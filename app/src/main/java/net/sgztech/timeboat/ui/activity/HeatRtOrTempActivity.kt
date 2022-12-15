package net.sgztech.timeboat.ui.activity

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.ActivityHeartRateDataBinding
import net.sgztech.timeboat.provide.dataModel.BottomResourceData
import net.sgztech.timeboat.ui.adapter.WalkDataPageAdapter
import net.sgztech.timeboat.ui.fragment.*
import net.sgztech.timeboat.ui.utils.UIUtils

class HeatRtOrTempActivity : BaseActivity(){

    private val heartRateBinding: ActivityHeartRateDataBinding by viewBinding()
    private var sportDateType =0
    override fun getLayoutId(): Int {
        return R.layout.activity_heart_rate_data
    }

    companion object {
        const val  heartRateType = 1000
        const val  temperatureType = 1001


    }

    override fun initBindView() {
        sportDateType =intent.getIntExtra(Constants.HOME_SPORT_DATA_TYPE,heartRateType)
        if(sportDateType ==temperatureType){
            heartRateBinding.heartRateTitleBar.titleName.text ="体温"
        }else{
            heartRateBinding.heartRateTitleBar.titleName.text ="心率"
        }

        heartRateBinding.heartRateTitleBar.backArrow.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        val bottomList = ArrayList<BottomResourceData>()
        bottomList.add(BottomResourceData("日", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("周", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("月", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("年", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        val mFragmentList = ArrayList<BaseFragment>()
        HeartRtOrTempDayFragment.newInstance("heartRtOrTempDay" ,sportDateType ).let { mFragmentList.add(it) }
        HeartRtOrTempWeekFragment.newInstance("heartRtOrTempWeek",sportDateType).let { mFragmentList.add(it) }
        HeartRtOrTempMonthFragment.newInstance("heartRtOrMonth",sportDateType).let { mFragmentList.add(it) }
        HeartRtOrTempYearFragment.newInstance("heartRtOrYear",sportDateType).let { mFragmentList.add(it) }
        heartRateBinding.heartRateContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
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

        heartRateBinding.heartRateContent.adapter =
            WalkDataPageAdapter(this@HeatRtOrTempActivity,mFragmentList)
        heartRateBinding.heartRateContent.offscreenPageLimit = mFragmentList.size - 1
        TabLayoutMediator(heartRateBinding.heartRateTabLayout,heartRateBinding.heartRateContent
        ) { tab, position ->
            {

            }
        }.attach()

        UIUtils.setHomeTablayoutCustomView(this@HeatRtOrTempActivity, heartRateBinding.heartRateTabLayout, bottomList)

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