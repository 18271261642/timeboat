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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.stockChart.charts.TimeLineChart
import com.github.mikephil.charting.stockChart.charts.TimeXAxis
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage
import com.github.mikephil.charting.stockChart.enums.ChartType
import com.github.mikephil.charting.stockChart.markerView.BarBottomMarkerView
import com.github.mikephil.charting.stockChart.markerView.LeftMarkerView
import com.github.mikephil.charting.stockChart.markerView.TimeRightMarkerView
import com.github.mikephil.charting.stockChart.renderer.ColorContentYAxisRenderer
import com.github.mikephil.charting.utils.CommonUtil
import com.github.mikephil.charting.utils.NumberUtils
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.StringsUtils
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.FragmentWalkMonthBinding
import net.sgztech.timeboat.provide.dataModel.TotalStepModel
import net.sgztech.timeboat.provide.viewModel.StepCountViewModel
import net.sgztech.timeboat.ui.activity.WalkDataActivity
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.calorieType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.distanceType
import net.sgztech.timeboat.ui.activity.WalkDataActivity.Companion.stepCountType
import net.sgztech.timeboat.ui.chart.CornerBarChart
import net.sgztech.timeboat.ui.chart.SportDataFormatter
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class WalkMonthFragment :BaseFragment()  {
    private val monthBinding :FragmentWalkMonthBinding by viewBinding()
    private var sportDataType:Int =0
    private val stepCountViewModel : StepCountViewModel by viewModels()
    private  lateinit var xAxis :XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var monthList = ArrayList<TotalStepModel>()

    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    companion object{

        fun newInstance(title: String,sportDataType :Int): WalkMonthFragment {
            val fragment = WalkMonthFragment()
            val args = Bundle()
            fragment.setLabel(title)
            args.putInt(Constants.FRAGMENT_ARG_TYPE, sportDataType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return  R.layout.fragment_walk_month
    }

    override fun initBindView() {
        monthBinding.selectDateLayout.dateSelect.text = DateUtil.getCurrentTime("yyyy-MM")
        if(arguments!=null&&arguments?.getInt(Constants.FRAGMENT_ARG_TYPE)!=null){
            sportDataType = arguments?.getInt(Constants.FRAGMENT_ARG_TYPE)!!
        }
        chatBar = monthBinding.walkMonthBar
        monthBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        monthBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        updateUIByType()
    }

    private fun updateUIByType(){

        when(sportDataType){
            WalkDataActivity.stepCountType ->
            {
                monthBinding.averageSportDescribe.text="平均步数(步)"
                monthBinding.totalSportDescribe.text="总步数(步)"
            }
            WalkDataActivity.distanceType ->{
                monthBinding.averageSportDescribe.text="平均距离(千米)"
                monthBinding.totalSportDescribe.text="总距离(千米)"
            }
            WalkDataActivity.calorieType ->  {
                monthBinding.averageSportDescribe.text="平均热量(千卡)"
                monthBinding.totalSportDescribe.text="总热量(千卡)"
            }
        }
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
                        WalkDataActivity.stepCountType -> {

                            monthBinding.totalSportCount.text= "" +it.info.totalStepCount
                            monthBinding.averageSportCount.text="" +it.info.averageStepCount
                        }
                        WalkDataActivity.distanceType -> {
                            val totalDistance=it.info.totalDistance.toDouble()/1000
                            monthBinding.totalSportCount.text= StringsUtils.getFormatDecimal(totalDistance,2)
                            val averageDistance=it.info.averageDistance.toDouble()/1000
                            monthBinding.averageSportCount.text=
                                StringsUtils.getFormatDecimal(averageDistance,2)
                        }
                        WalkDataActivity.calorieType -> {
                            val totalCalorie=it.info.totalCalorie.toDouble()/1000
                            monthBinding.totalSportCount.text= StringsUtils.getFormatDecimal(totalCalorie,2)
                            val averageCalorie=it.info.averageCalorie.toDouble()/1000
                            monthBinding.averageSportCount.text=
                                StringsUtils.getFormatDecimal(averageCalorie,2)
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
        chatBar.isDragEnabled = false
        chatBar.setScaleEnabled(false)
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);
        //取消描述文字
        chatBar.description.isEnabled = false;
        //没有数据时显示的文字
        chatBar.setNoDataText("没有运动数据");//没有数据时显示的文字
        //没有数据时显示文字的颜色
        chatBar.setNoDataTextColor(Color.RED);//没有数据时显示文字的颜色
        // force pinch zoom along both axis
        chatBar.setPinchZoom(false)
        //设置 chart 边框线的颜色。
        chatBar.setBorderColor(Color.WHITE);
        //设置 chart 边界线的宽度，单位 dp。
        chatBar.setBorderWidth(3f);
        chatBar.axisRight.isEnabled = false
        chatBar.legend.isEnabled = false
        chatBar.invalidate()
//        testDate(7 ,40.toFloat())
        observer()
        selectTimeStamp = System.currentTimeMillis()
        stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.monthlyType)
    }


    private fun setUpXAxis(){
        xAxis = chatBar.xAxis
        //x轴刻度值显示在底部
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false)
//        xAxisLine.enableGridDashedLine(10f, 10f, 0f)
//        xAxisLine.setGridLineWidth(0.7f)
//        xAxisLine.setAxisMaximum(60*24f)
//        xAxisLine.setAxisMinimum(0f)
        //x轴颜色
        xAxis.axisLineColor = R.color.x_axis_color
        //下周下表颜色
        xAxis.textColor = Color.BLACK
        //是否绘制坐标线
        xAxis.setDrawAxisLine(true)
        //是否绘制轴的网格线
        xAxis.setDrawGridLines(false)
        //是否绘制坐标值
        xAxis.setDrawLabels(true)
        //设置X轴的值的间隔
        xAxis.setGranularity(1f)

//        xAxis.setLabelCount(7, true)

        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getBarLabel(barEntry: BarEntry?): String {
                return super.getBarLabel(barEntry)
            }

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                try {
                    if(monthList!=null && monthList.size>value.toInt()){
                        var time =0L
                        for(item in monthList){
                            if(item.id.toFloat() ==value){
                                var date = StringsUtils.toYmdDDate(monthList[value.toInt()].date )
                                time =date.time
                                break
                            }
                        }
                        return SimpleDateFormat("MM.dd").format(time)
                    }else{
                        return SimpleDateFormat("MM.dd").format(System.currentTimeMillis())
                    }
                }catch (e:Exception){
                    return ""
                }

            }
        }
    }


    private fun setUpYAxis(){

        yAxis = chatBar.axisLeft
        yAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        //leftAxis.setYOffset(20f);
        //设置Y轴网格线条的虚线，参1 实线长度，参2 虚线长度 ，参3 周期
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        // 网格线条间距
        //yAxisLine.setGranularity(20f);
        //设置Y轴数值 从零开始
        yAxis.setDrawZeroLine(false)
        //网格线条的颜色
        yAxis.gridColor = Color.parseColor("#707070");  //网格线条的颜色

        // limit lines are drawn behind data (and not on top)
        yAxis.setDrawLimitLinesBehindData(true)
        // axis range
//        yAxisLine.axisMaximum = 200f
        yAxis.axisMinimum = 0f
        //右边y轴线


        yAxis.setLabelCount(6, true)
        yAxis.textColor = R.color.y_axis_color
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //设置y轴上每个点对应的线
        yAxis.setDrawGridLines(true)
        //是否显示Y轴刻度
        yAxis.setDrawLabels(true)
        //是否绘制轴线
        yAxis.setDrawAxisLine(false);
        yAxis.axisLineColor = Color.BLACK
        when(sportDataType){
            distanceType, calorieType -> {
                yAxis.valueFormatter = SportDataFormatter()
            }
        }
    }


    private fun getSportEntry(sourceData :List<TotalStepModel>): List<BarEntry>{

        val values = ArrayList<BarEntry>()
        for ( i in sourceData.indices){

            sourceData[i].id= i
            when(sportDataType){
                stepCountType -> values.add(BarEntry(i.toFloat(), sourceData[i].stepCount.toFloat()))
                distanceType ->values.add(BarEntry(i.toFloat(), sourceData[i].distance.toFloat()))
                calorieType -> values.add(BarEntry(i.toFloat(), sourceData[i].calorie.toFloat()))
            }
        }
        if(sourceData.isNotEmpty()){
            monthList = sourceData as ArrayList<TotalStepModel>
        }

        return values
    }

    private fun updateSportData(data : List<TotalStepModel>){
        val entryList =getSportEntry(data)
        setData(entryList)
    }

    private fun setData(values : List<BarEntry>) {
        if(values.isNotEmpty()){
            val set1: BarDataSet
            if (chatBar.data != null && chatBar.data!!.dataSetCount > 0) {
                set1 = chatBar.data!!.getDataSetByIndex(0) as BarDataSet
                set1.values = values
                chatBar.data!!.notifyDataChanged()
                chatBar.notifyDataSetChanged()
                chatBar.invalidate()
            } else {
                set1 = BarDataSet(values, "The Month")
                set1.setDrawIcons(false)
//            set1.color = R.color.orange_end
                set1.color = ContextCompat.getColor(mContext, R.color.colour_orange)
//            set1.increasingColor = ContextCompat.getColor(mContext, R.color.orange_end)
//            set1.increasingPaintStyle = Paint.Style.FILL
                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(set1)
                val data = BarData(dataSets)
                data.setValueTextSize(10f)
//            data.setValueTypeface(tfLight)
                data.barWidth = 0.4f
                chatBar.setData(data)
                chatBar.invalidate()
            }
        }else{
            if (chatBar.data != null &&
                chatBar.data.dataSetCount > 0) {
                chatBar.clearValues();
                chatBar.invalidate();
            }
        }

    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.left_arrow_date ->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    selectTimeStamp = DateUtil.getLastMonthTimeStamp(selectTimeStamp)
                    monthBinding.selectDateLayout.dateSelect.text = DateUtil.getTimeStampYM(selectTimeStamp)
                    stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp),
                        Constants.monthlyType
                    )
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    var nextMonth =DateUtil.getNextMonthTimeStamp(selectTimeStamp)
                    if(nextMonth < System.currentTimeMillis()){
                        selectTimeStamp = nextMonth
                        monthBinding.selectDateLayout.dateSelect.text = DateUtil.getTimeStampYM(selectTimeStamp)
                        stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.monthlyType)
                    }else{
                        UToast.showShortToast("只能查看历史数据")
                    }
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }
        }
    }


    private fun testDate(count: Int, range: Float) {
        val start = 0
        val values = ArrayList<BarEntry>()
        var i = start
        val oneDay = 24*60*60*1000
        while (i < start + count) {
            val data = (Math.random() * (range + 1)).toInt()
            var item = TotalStepModel()
//            val timeStamp =System.currentTimeMillis() -i*oneDay
            item.id= i
            if(i ==0){
                item.date= "2022-08-05 16:39:25:193"
            }else if(i==1){
                item.date= "2022-08-06 16:39:25:193"
            }else if(i==2){
                item.date= "2022-08-07 16:39:25:193"
            }else if(i==3){
                item.date= "2022-08-08 16:39:25:193"
            }else if(i==4){
                item.date= "2022-08-09 16:39:25:193"
            }else if(i==5){
                item.date= "2022-08-10 16:39:25:193"
            }else if(i==6){
                item.date= "2022-08-11 16:39:25:193"
            }



            item.stepCount =data
            item.distance =data
            item.calorie =data
            monthList.add(item)

            values.add(BarEntry(i.toFloat(), data.toFloat(),R.drawable.fade_red))
            i++
        }
        setData(values)
    }

}