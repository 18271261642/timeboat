package net.sgztech.timeboat.ui.fragment

import android.graphics.Color
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.StringsUtils
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.FragmentSleepDayBinding
import net.sgztech.timeboat.databinding.FragmentSleepWeekBinding
import net.sgztech.timeboat.provide.dataModel.*
import net.sgztech.timeboat.provide.viewModel.SleepStatisticsViewModel
import net.sgztech.timeboat.ui.activity.HeatRtOrTempActivity
import net.sgztech.timeboat.ui.chart.CornerBarChart
import java.text.SimpleDateFormat
import kotlin.math.min

class SleepWeekFragment : BaseFragment(){
    private val weekBinding : FragmentSleepWeekBinding by viewBinding()
    private val  sleepViewModel :SleepStatisticsViewModel by viewModels()
    private  lateinit var xAxis : XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var weekList = ArrayList<SleepStatisticsModel>()
    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    companion object{
        fun newInstance(label: String ): SleepWeekFragment {
            val fragment = SleepWeekFragment()
            fragment.setLabel(label)
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_sleep_week
    }

    override fun initBindView() {
        weekBinding.selectDateLayout.dateSelect.text = "??????"
        chatBar =weekBinding.sleepWeekChart
        weekBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        weekBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
    }

    private fun observer(){

        sleepViewModel.sleepStatisticsData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null){
                    updateSportData(it.list)
                    weekBinding.soberSleepTime.text = changeTimeFormat(it.info.totalSoberMinute)
                    weekBinding.deepSleepTime.text = changeTimeFormat(it.info.totalDeepSleepMinute)
                    weekBinding.lightSleepTime.text = changeTimeFormat(it.info.totalLightSleepMinute)
                    weekBinding.averageSleepTime.text= changeTimeFormat(it.info.totalMinute)
                }else{
                    if (chatBar.data != null && chatBar.data.dataSetCount > 0) {
                        chatBar.clearValues()
                        chatBar.invalidate()
                    }

                    weekBinding.soberSleepTime.text = "--"
                    weekBinding.deepSleepTime.text = "--"
                    weekBinding.lightSleepTime.text = "--"
                    weekBinding.averageSleepTime.text= "--"
                }

            }
            onAppError = { msg, errorCode ->

            }

            onAppComplete ={

            }

        }

    }

    private fun changeTimeFormat(minute :Int) :String{

        var hour = minute/60 ;
        var minute = minute%60
        return if(hour>0 ){
            ""+  hour + "H" + minute +"M"
        }else {
            ""+ minute +"M"
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
        requestData()
    }

    private fun requestData(){
        sleepViewModel.getSleepStatisticsData(DateUtil.getTimeStampYMD(selectTimeStamp),Constants.weeklyType)
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
                    return  ""
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

    }


    private fun getSportEntry(sourceData :List<SleepStatisticsModel>): List<BarEntry>{
        val values = java.util.ArrayList<BarEntry>()

        for ( i in sourceData.indices){
            sourceData[i].id= i
            values.add(BarEntry(i.toFloat(), sourceData[i].totalMinute.toFloat()))
        }
        if(sourceData.isNotEmpty()){
            weekList = sourceData as ArrayList<SleepStatisticsModel>
        }

        return values
    }



    private fun updateSportData(data : List<SleepStatisticsModel>){
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
                chatBar.clearValues()
                chatBar.invalidate()
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
                    requestData()
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
                        requestData()
                    }
                }else{
                    UToast.showShortToast("???????????????")
                }
            }
        }

    }
}