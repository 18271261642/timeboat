package net.sgztech.timeboat.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.stockChart.charts.TimeXAxis
import com.github.mikephil.charting.utils.CommonUtil
import com.github.mikephil.charting.utils.DataTimeUtil
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.StringsUtils
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.FRAGMENT_ARG_TYPE
import net.sgztech.timeboat.config.Constants.Companion.dailyType
import net.sgztech.timeboat.config.Constants.Companion.oneDay
import net.sgztech.timeboat.databinding.FragmentWalkDayBinding
import net.sgztech.timeboat.provide.dataModel.TotalStepModel
import net.sgztech.timeboat.provide.viewModel.StepCountViewModel
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.calorieType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.distanceType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.stepCountType
import net.sgztech.timeboat.ui.chart.SportDataFormatter
import net.sgztech.timeboat.ui.chart.SportLineChart
import java.util.*

class WalkDayFragment :BaseFragment() {

    private val stepCountViewModel :StepCountViewModel by viewModels()
    private val walkDayBinding :FragmentWalkDayBinding by viewBinding()

    private var xLabels = SparseArray<String>() //X轴刻度label
    private lateinit var  xAxisLine :TimeXAxis
    private lateinit var  yAxisLine :YAxis
    private lateinit var  dayChat  :SportLineChart
    private var sportDataType:Int =0
    private val maxXAxisValue = 60*24
    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    companion object{
        fun newInstance(label: String ,sportDataType :Int): WalkDayFragment {
            val fragment = WalkDayFragment()
            val args = Bundle()
            fragment.setLabel(label)
            args.putInt(FRAGMENT_ARG_TYPE, sportDataType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
       return R.layout.fragment_walk_day
    }

    override fun initBindView() {
        walkDayBinding.selectDateLayout.dateSelect.text = DateUtil.getCurrentTime()
        dayChat =walkDayBinding.walkDayChart
        if(arguments!=null&&arguments?.getInt(FRAGMENT_ARG_TYPE)!=null){
            sportDataType = arguments?.getInt(FRAGMENT_ARG_TYPE)!!
        }

        walkDayBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        walkDayBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        updateUIByType()
    }

    private fun observer(){

        stepCountViewModel.stepData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                 if(it!=null&&it.list!=null){
                     updateSportData(it.list )
//                    setData()
                     when(sportDataType){

                         stepCountType -> {
                             walkDayBinding.sportCount.text= "" + it.info.totalStepCount
                         }
                         distanceType -> {
                             val totalDistance=it.info.totalDistance.toDouble()/1000
                             walkDayBinding.sportCount.text= StringsUtils.getFormatDecimal(totalDistance,2)
                         }

                         calorieType->  {
                             val totalCalorie=it.info.totalCalorie.toDouble()/1000
                             walkDayBinding.sportCount.text= StringsUtils.getFormatDecimal(totalCalorie,2)
                         }
                     }
                 }

            }
            onAppError = { msg, errorCode ->

            }

            onAppComplete ={

            }

        }

    }


    override fun initData() {
        setUpXAxis()
        setUpYAxis()
        // enable scaling and dragging

        // enable scaling and dragging
        dayChat.isDragEnabled = false
        dayChat.setScaleEnabled(false)
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);
        //取消描述文字
        dayChat.description.isEnabled = false;
        //没有数据时显示的文字
        dayChat.setNoDataText("没有运动数据");//没有数据时显示的文字
        //没有数据时显示文字的颜色
        dayChat.setNoDataTextColor(Color.RED);//没有数据时显示文字的颜色
        // force pinch zoom along both axis
        dayChat.setPinchZoom(false)
        //设置 chart 边框线的颜色。
        dayChat.setBorderColor(Color.WHITE);
        //设置 chart 边界线的宽度，单位 dp。
        dayChat.setBorderWidth(3f);
        dayChat.legend.isEnabled = false
        dayChat.invalidate()
        observer()
        selectTimeStamp = System.currentTimeMillis()
        stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), dailyType)

//        setData(45 ,100.toFloat())
//        testUI(100 ,maxXAxisValue.toFloat())

    }

    private fun updateUIByType(){

        when(sportDataType){
            stepCountType -> walkDayBinding.sportDescribe.text="总步数(步)"
            distanceType ->walkDayBinding.sportDescribe.text="总距离(千米)"
            calorieType->  walkDayBinding.sportDescribe.text="总热量(千卡)"
        }

    }

    private fun setUpXAxis(){
        xAxisLine = dayChat.xAxis as TimeXAxis
        //x轴刻度值显示在底部
        xAxisLine.position = XAxis.XAxisPosition.BOTTOM
        //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxisLine.setAvoidFirstLastClipping(false)
//        xAxisLine.enableGridDashedLine(10f, 10f, 0f)
//        xAxisLine.setGridLineWidth(0.7f)
        xAxisLine.axisMaximum = maxXAxisValue.toFloat()
        xAxisLine.axisMinimum = 0f
        //x轴颜色
        xAxisLine.axisLineColor = R.color.x_axis_color
        //下周下表颜色
        xAxisLine.textColor = Color.BLACK
        //是否绘制坐标线
        xAxisLine.setDrawAxisLine(true)
        //是否绘制轴的网格线
        xAxisLine.setDrawGridLines(false)
        //是否绘制坐标值
        xAxisLine.setDrawLabels(true)
        xAxisLine.setAvoidFirstLastClipping(true)

    }

    private fun setUpYAxis(){

        yAxisLine = dayChat.axisLeft
        yAxisLine.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        //leftAxis.setYOffset(20f);
        //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        yAxisLine.enableGridDashedLine(10f, 10f, 0f)
        // 网格线条间距
        //yAxisLine.setGranularity(20f);
        //设置Y轴数值 从零开始
        yAxisLine.setDrawZeroLine(false)
        //网格线条的颜色
        yAxisLine.gridColor = Color.parseColor("#707070");  //网格线条的颜色

        // limit lines are drawn behind data (and not on top)
        yAxisLine.setDrawLimitLinesBehindData(true)
        // axis range
//        yAxisLine.axisMaximum = 200f
        yAxisLine.axisMinimum = 0f
        //右边y轴线
        dayChat.axisRight.isEnabled = false

        yAxisLine.setLabelCount(6, true)
        yAxisLine.textColor = R.color.y_axis_color
        yAxisLine.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //设置y轴上每个点对应的线
        yAxisLine.setDrawGridLines(true)
        //是否显示Y轴刻度
        yAxisLine.setDrawLabels(true)
        //是否绘制轴线
        yAxisLine.setDrawAxisLine(false);
        yAxisLine.axisLineColor = Color.BLACK

        when(sportDataType){

            distanceType,calorieType-> {
                yAxisLine.valueFormatter = SportDataFormatter()
            }
        }
    }

    fun getXLabels(): SparseArray<String> {
        if (xLabels.size() == 0) {
            xLabels.put(0, "0:00")
            xLabels.put(60*4, "4:00")
            xLabels.put(60*8, "8:00")
            xLabels.put(60*12, "12:00")
            xLabels.put(60*16, "16:00")
            xLabels.put(60*20, "20:00")
            xLabels.put(maxXAxisValue, "24:00")
        }

        return xLabels
    }


    /**
     * 是否显示坐标轴label
     *
     * @param isShow
     */
    private fun setShowLabels(isShow: Boolean) {
        dayChat.axisLeft.setDrawLabels(isShow)
        dayChat.axisRight.setDrawLabels(isShow)
        dayChat.xAxis.setDrawLabels(isShow)
    }


    private fun changeDateToId(dateString :String):Int{
        var date =StringsUtils.toDate(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        return hour * 60 + minute
    }

    private fun getSportEntry(sourceData :List<TotalStepModel>): List<Entry>{
        val values = ArrayList<Entry>()
        for (item  in sourceData){
            val id =  changeDateToId(item.deviceTime)
            when(sportDataType){
                stepCountType-> values.add(Entry(id.toFloat(), item.stepCount.toFloat()))
                distanceType->values.add(Entry(id.toFloat(), item.distance.toFloat()))
                calorieType-> values.add(Entry(id.toFloat(), item.calorie.toFloat()))
            }
        }
        return values
    }

    private fun updateSportData(data : List<TotalStepModel>){
        val entryList =getSportEntry(data)
        setData(entryList)
    }

    private fun setData(values : List<Entry>) {
        if(values.isNotEmpty()){
            val set1: LineDataSet
            if (dayChat.data != null &&
                dayChat.data.dataSetCount > 0) {

                set1 = dayChat.data.getDataSetByIndex(0) as LineDataSet
                set1.values = values

                set1.notifyDataSetChanged()
                dayChat.data.notifyDataChanged()
                dayChat.notifyDataSetChanged()
                dayChat.invalidate()

            } else {

                setXLabels(getXLabels())
                setShowLabels(true)

                // create a dataset and give it a type
                set1 = LineDataSet(values, "运动数据")
                set1.setDrawCircleHole(false)
                set1.setDrawCircleDashMarker(false)
                set1.xLabels =getXLabels()
                setShowLabels(true)
                set1.setDrawValues(false)
                set1.setDrawFilled(true)
//                set1.setCircleColor(Color.WHITE)
                set1.color = R.color.orange_end


                set1.setDrawHorizontalHighlightIndicator(false)
                set1.setDrawCircleDashMarker(false)

                val drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_fill_color)
                set1.fillDrawable = drawable
                set1.highLightColor = ContextCompat.getColor(mContext, R.color.highLight_Color)
                set1.isHighlightEnabled = false //是否显示高亮十字线

                set1.lineWidth = 2f
                set1.circleRadius = 3f
                set1.setDrawCircles(true)
                val color =ContextCompat.getColor(mContext, R.color.orange_end)
                set1.setCircleColor(color)
                set1.color = color

                set1.axisDependency = YAxis.AxisDependency.LEFT
                set1.precision = 2
                // create a data object with the data sets
                val data = LineData(set1)
//            data.setValueTypeface(tfLight)
                data.setValueTextSize(9f)
//            data.setDrawValues(true)
                //下面方法需在填充数据后调用
                xAxisLine.setXLabels(getXLabels())

                xAxisLine.setLabelCount(getXLabels().size(), false)
                setShowLabels(true)
                dayChat.setViewPortOffsets(
                    CommonUtil.dip2px(mContext, 45f).toFloat(),
                    CommonUtil.dip2px(mContext, 30f).toFloat(),
                    CommonUtil.dip2px(mContext, 30f).toFloat(),
                    CommonUtil.dip2px(mContext, 30f).toFloat()
                )
                // set data
                dayChat.data = data
                dayChat.setVisibleXRange(maxXAxisValue.toFloat(), maxXAxisValue.toFloat())
                //moveViewTo(...) 方法会自动调用 invalidate()
                // redraw
                dayChat.moveViewToX((values.size - 1).toFloat())
//            walkDayBinding.walkDayChart.invalidate()

            }
        }else{
            if (dayChat.data != null &&
                dayChat.data.dataSetCount > 0) {
                dayChat.clearValues();
                dayChat.invalidate();
            }
        }
    }

    fun setXLabels(xLabels: SparseArray<String>) {
        this.xLabels = xLabels
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.left_arrow_date ->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    selectTimeStamp -= oneDay
                    walkDayBinding.selectDateLayout.dateSelect.text = DateUtil.getTimeStampYMD(selectTimeStamp)
                    stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), dailyType)
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    if((selectTimeStamp + oneDay)<System.currentTimeMillis()){
                        selectTimeStamp += oneDay
                        walkDayBinding.selectDateLayout.dateSelect.text =  DateUtil.getTimeStampYMD(selectTimeStamp)
                        stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), dailyType)
                    }else{
                        UToast.showShortToast("只能查看历史数据")
                    }
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }

        }

    }

    private fun testUI(count :Int, range: Float){
        setData(testEntry(count,range))
    }
    private fun testEntry(count: Int, range: Float) :List<Entry>{
        val values = ArrayList<Entry>()
        for (i in 0 until count) {
            val value = (Math.random() * (count + 1)).toFloat() + 20
            var id = range*i/100
            values.add(Entry(id.toFloat(), value))
        }
        return values
    }

}