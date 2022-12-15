package net.sgztech.timeboat.ui.activity

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import net.sgztech.timeboat.R
import net.sgztech.timeboat.databinding.ActivityBloodOxDataBinding
import net.sgztech.timeboat.provide.dataModel.BottomResourceData
import net.sgztech.timeboat.ui.adapter.WalkDataPageAdapter
import net.sgztech.timeboat.ui.fragment.*
import net.sgztech.timeboat.ui.utils.UIUtils

class BloodOxDataActivity :BaseActivity(){

    private val bloodOxBinding: ActivityBloodOxDataBinding by viewBinding()

    override fun getLayoutId(): Int {
        return R.layout.activity_blood_ox_data
    }

    override fun initBindView() {
        bloodOxBinding.bloodOxTitleBar.backArrow.setOnClickListener(this)
        bloodOxBinding.bloodOxTitleBar.titleName.text ="血氧"
    }

    override fun initData() {
        super.initData()
        val bottomList = ArrayList<BottomResourceData>()
        bottomList.add(BottomResourceData("日", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("周", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("月", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("年", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        val mFragmentList = ArrayList<BaseFragment>()
        BloodOxDayFragment.newInstance("bloodOxDay" ).let { mFragmentList.add(it) }
        BloodOxWeekFragment.newInstance("bloodOxWeek").let { mFragmentList.add(it) }
        BloodOxMonthFragment.newInstance("bloodOxMonth").let { mFragmentList.add(it) }
        BloodOxYearFragment.newInstance("bloodOxYear").let { mFragmentList.add(it) }
        bloodOxBinding.bloodOxContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
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

        bloodOxBinding.bloodOxContent.adapter =
            WalkDataPageAdapter(this@BloodOxDataActivity,mFragmentList)
        bloodOxBinding.bloodOxContent.offscreenPageLimit = mFragmentList.size - 1
        TabLayoutMediator(bloodOxBinding.bloodOxTabLayout,bloodOxBinding.bloodOxContent
        ) { tab, position ->
            {

            }
        }.attach()

        UIUtils.setHomeTablayoutCustomView(this@BloodOxDataActivity, bloodOxBinding.bloodOxTabLayout, bottomList)

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