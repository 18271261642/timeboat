package net.sgztech.timeboat.ui.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.StringsUtils
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.bleCommand.StepCountDataModel
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.FragmentWalkWeekBinding
import net.sgztech.timeboat.provide.dataModel.TotalStepModel
import net.sgztech.timeboat.provide.viewModel.StepCountViewModel
import net.sgztech.timeboat.ui.activity.WalkDataActivity
import net.sgztech.timeboat.ui.chart.CornerBarChart
import net.sgztech.timeboat.ui.chart.SportDataFormatter
import java.text.SimpleDateFormat

class WalkWeekFragment:BaseFragment() {
    private val weekBinding : FragmentWalkWeekBinding by viewBinding()
    private val stepCountViewModel :StepCountViewModel by viewModels()
    private  lateinit var xAxis :XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var sportDataType:Int =0
    private var weekList = ArrayList<TotalStepModel>()

    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    companion object{
        fun newInstance(label: String ,sportDataType :Int): WalkWeekFragment {
            val fragment = WalkWeekFragment()
            val args = Bundle()
            fragment.setLabel(label)
            args.putInt(Constants.FRAGMENT_ARG_TYPE, sportDataType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_walk_week
    }

    override fun initBindView() {
        weekBinding.selectDateLayout.dateSelect.text = "??????"
        if(arguments!=null&&arguments?.getInt(Constants.FRAGMENT_ARG_TYPE)!=null){
            sportDataType = arguments?.getInt(Constants.FRAGMENT_ARG_TYPE)!!
        }
        weekBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        weekBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        chatBar =weekBinding.walkWeekBar
        updateUIByType()
    }


    private fun updateUIByType(){

        when(sportDataType){
            WalkDataActivity.stepCountType ->
            {
                weekBinding.averageSportDescribe.text="????????????(???)"
                weekBinding.totalSportDescribe.text="?????????(???)"
            }
            WalkDataActivity.distanceType ->{
                weekBinding.averageSportDescribe.text="????????????(??????)"
                weekBinding.totalSportDescribe.text="?????????(??????)"
            }
            WalkDataActivity.calorieType ->  {
                weekBinding.averageSportDescribe.text="????????????(??????)"
                weekBinding.totalSportDescribe.text="?????????(??????)"
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

                            weekBinding.totalSportCount.text= "" +it.info.totalStepCount
                            weekBinding.averageSportCount.text="" +it.info.averageStepCount

                        }
                        WalkDataActivity.distanceType -> {
                            val totalDistance=it.info.totalDistance.toDouble()/1000
                            weekBinding.totalSportCount.text= StringsUtils.getFormatDecimal(totalDistance,2)
                            val averageDistance=it.info.averageDistance.toDouble()/1000
                            weekBinding.averageSportCount.text=StringsUtils.getFormatDecimal(averageDistance,2)
                        }
                        WalkDataActivity.calorieType -> {
                            val totalCalorie=it.info.totalCalorie.toDouble()/1000
                            weekBinding.totalSportCount.text= StringsUtils.getFormatDecimal(totalCalorie,2)
                            val averageCalorie=it.info.averageCalorie.toDouble()/1000
                            weekBinding.averageSportCount.text=StringsUtils.getFormatDecimal(averageCalorie,2)
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
        //??????????????????
        chatBar.description.isEnabled = false;
        //??????????????????????????????
        chatBar.setNoDataText("??????????????????");//??????????????????????????????
        //????????????????????????????????????
        chatBar.setNoDataTextColor(Color.RED);//????????????????????????????????????
        // force pinch zoom along both axis
        chatBar.setPinchZoom(false)
        //?????? chart ?????????????????????
        chatBar.setBorderColor(Color.WHITE);
        //?????? chart ??????????????????????????? dp???
        chatBar.setBorderWidth(3f);
        chatBar.axisRight.isEnabled = false
        chatBar.legend.isEnabled = false
        chatBar.invalidate()
//        testDate(7 ,40.toFloat())
        observer()
        selectTimeStamp = System.currentTimeMillis()
        stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.weeklyType)
    }


    private fun setUpXAxis(){
        xAxis = chatBar.xAxis
        //x???????????????????????????
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //???????????????????????????????????????????????????????????????????????????????????????
        xAxis.setAvoidFirstLastClipping(false)
//        xAxisLine.enableGridDashedLine(10f, 10f, 0f)
//        xAxisLine.setGridLineWidth(0.7f)
//        xAxisLine.setAxisMaximum(60*24f)
//        xAxisLine.setAxisMinimum(0f)
        //x?????????
        xAxis.axisLineColor = R.color.x_axis_color
        //??????????????????
        xAxis.textColor = Color.BLACK
        //?????????????????????
        xAxis.setDrawAxisLine(true)
        //???????????????????????????
        xAxis.setDrawGridLines(false)
        //?????????????????????
        xAxis.setDrawLabels(true)
        //??????X??????????????????
        xAxis.setGranularity(1f)

//        xAxis.setLabelCount(7, true)

        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getBarLabel(barEntry: BarEntry?): String {
                return super.getBarLabel(barEntry)
            }

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                try {
                    if(weekList!=null && weekList.size>value.toInt()){
                        var time =0L
                        for(item in weekList){
                            if(item.id.toFloat() ==value){
                                //?????????date ??????
                                var date = StringsUtils.toYmdDDate(weekList[value.toInt()].date)
                                time =date.time
                                break
                            }
                        }
                        return SimpleDateFormat("MM.dd").format(time)
                    }else{
                        return SimpleDateFormat("MM.dd").format(System.currentTimeMillis())
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    return ""
                }

            }
        }
    }


    private fun setUpYAxis(){

        yAxis = chatBar.axisLeft
        yAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        //leftAxis.setYOffset(20f);
        //??????Y??????????????????????????????1 ??????????????????2 ???????????? ??????3 ??????
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        // ??????????????????
        //yAxisLine.setGranularity(20f);
        //??????Y????????? ????????????
        yAxis.setDrawZeroLine(false)
        //?????????????????????
        yAxis.gridColor = Color.parseColor("#707070");  //?????????????????????

        // limit lines are drawn behind data (and not on top)
        yAxis.setDrawLimitLinesBehindData(true)
        // axis range
//        yAxisLine.axisMaximum = 200f
        yAxis.axisMinimum = 0f
        //??????y??????


        yAxis.setLabelCount(6, true)
        yAxis.textColor = R.color.y_axis_color
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //??????y???????????????????????????
        yAxis.setDrawGridLines(true)
        //????????????Y?????????
        yAxis.setDrawLabels(true)
        //??????????????????
        yAxis.setDrawAxisLine(false);
        yAxis.axisLineColor = Color.BLACK
        when(sportDataType){
            WalkDataActivity.distanceType, WalkDataActivity.calorieType -> {
                yAxis.valueFormatter = SportDataFormatter()
            }
        }
    }


    private fun getSportEntry(sourceData :List<TotalStepModel>): List<BarEntry>{

        val values = ArrayList<BarEntry>()
        for ( i in sourceData.indices){

            sourceData[i].id= i
            when(sportDataType){
                WalkDataActivity.stepCountType -> values.add(BarEntry(i.toFloat(), sourceData[i].stepCount.toFloat()))
                WalkDataActivity.distanceType ->values.add(BarEntry(i.toFloat(), sourceData[i].distance.toFloat()))
                WalkDataActivity.calorieType -> values.add(BarEntry(i.toFloat(), sourceData[i].calorie.toFloat()))
            }
        }
        if(sourceData.isNotEmpty()){
            weekList = sourceData as ArrayList<TotalStepModel>
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
               set1.notifyDataSetChanged()
               chatBar.data!!.notifyDataChanged()
               chatBar.notifyDataSetChanged()
               chatBar.invalidate()
           } else {
               set1 = BarDataSet(values, "The week")
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
                    selectTimeStamp -= Constants.oneWeek
                    weekBinding.selectDateLayout.dateSelect.text = DateUtil.getWeekString(selectTimeStamp)
                    stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp),
                        Constants.weeklyType
                    )
                }else{
                    UToast.showShortToast("???????????????")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()

                    val currentWeek =DateUtil.getWeek(currentTimeStamp)
                    val selectWeek = DateUtil.getWeek(selectTimeStamp)
                    if(currentWeek ==selectWeek){
                        UToast.showShortToast("????????????????????????")
                    }else{
                        selectTimeStamp += Constants.oneWeek
                        val selectNextWeek = DateUtil.getWeek(selectTimeStamp)
                        if(selectNextWeek==currentWeek){
                            weekBinding.selectDateLayout.dateSelect.text = "??????"
                        }else{
                            weekBinding.selectDateLayout.dateSelect.text = DateUtil.getWeekString(selectTimeStamp)
                        }
                        stepCountViewModel.getStepData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.weeklyType)
                    }
                }else{
                    UToast.showShortToast("???????????????")
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
            weekList.add(item)

            values.add(BarEntry(i.toFloat(), data.toFloat(),R.drawable.fade_red))
            i++
        }
        setData(values)
    }


}