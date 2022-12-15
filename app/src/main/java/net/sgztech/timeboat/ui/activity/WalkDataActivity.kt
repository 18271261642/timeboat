package net.sgztech.timeboat.ui.activity

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants.Companion.HOME_SPORT_DATA_TYPE
import net.sgztech.timeboat.databinding.ActiviyWalkDataBinding
import net.sgztech.timeboat.provide.dataModel.BottomResourceData
import net.sgztech.timeboat.ui.adapter.WalkDataPageAdapter
import net.sgztech.timeboat.ui.fragment.WalkDayFragment
import net.sgztech.timeboat.ui.fragment.WalkMonthFragment
import net.sgztech.timeboat.ui.fragment.WalkWeekFragment
import net.sgztech.timeboat.ui.fragment.WalkYearFragment
import net.sgztech.timeboat.ui.utils.UIUtils


class WalkDataActivity:BaseActivity(){

    private val walkDateBinding:ActiviyWalkDataBinding by viewBinding()
    private var sportDateType =0
    companion object {
           const val  stepCountType = 0
           const val  distanceType = 1
           const val  calorieType = 2

    }

    override fun getLayoutId(): Int {
       return R.layout.activiy_walk_data
    }

    override fun initBindView() {
        sportDateType =intent.getIntExtra(HOME_SPORT_DATA_TYPE ,0)
        walkDateBinding.walkTitleBar.backArrow.setOnClickListener(this)
        when(sportDateType){
            stepCountType -> walkDateBinding.walkTitleBar.titleName.text ="步数"
            distanceType -> walkDateBinding.walkTitleBar.titleName.text ="距离"
            calorieType -> walkDateBinding.walkTitleBar.titleName.text ="热量"
        }

    }

    override fun initData() {
        super.initData()
        var bottomList = ArrayList<BottomResourceData>()
        bottomList.add(BottomResourceData("日", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("周", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("月", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        bottomList.add(BottomResourceData("年", R.drawable.btn_corner_white, R.drawable.btn_corner_orange_transparent))
        val mFragmentList = ArrayList<BaseFragment>()
        WalkDayFragment.newInstance("walkDay" ,sportDateType).let { mFragmentList.add(it) }
        WalkWeekFragment.newInstance("walkWeek",sportDateType).let { mFragmentList.add(it) }
        WalkMonthFragment.newInstance("walkMonth",sportDateType).let { mFragmentList.add(it) }
        WalkYearFragment.newInstance("walkYear",sportDateType).let { mFragmentList.add(it) }
        walkDateBinding.walkContent.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
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

        walkDateBinding.walkContent.adapter =WalkDataPageAdapter(this@WalkDataActivity,mFragmentList)
        walkDateBinding.walkContent.offscreenPageLimit = mFragmentList.size - 1
        TabLayoutMediator(walkDateBinding.walkTabLayout,walkDateBinding.walkContent,TabConfigurationStrategy{
                tab, position->{

        }
        }).attach()

        UIUtils.setHomeTablayoutCustomView(this@WalkDataActivity, walkDateBinding.walkTabLayout, bottomList)

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