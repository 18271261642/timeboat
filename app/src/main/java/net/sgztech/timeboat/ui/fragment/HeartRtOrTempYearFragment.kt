package net.sgztech.timeboat.ui.fragment


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
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
import net.sgztech.timeboat.databinding.FragmentHeartRateYearBinding
import net.sgztech.timeboat.provide.dataModel.BodyTemperatureModel
import net.sgztech.timeboat.provide.dataModel.HeartRateModel
import net.sgztech.timeboat.provide.dataModel.XAxisValueModel
import net.sgztech.timeboat.provide.viewModel.HeartRateViewModel
import net.sgztech.timeboat.ui.activity.HeatRtOrTempActivity
import net.sgztech.timeboat.ui.chart.CornerBarChart
import java.text.SimpleDateFormat

class HeartRtOrTempYearFragment : BaseFragment() {
    private val yearBinding: FragmentHeartRateYearBinding by viewBinding()
    private val heartRateViewModel : HeartRateViewModel by viewModels()
    private  lateinit var xAxis : XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var sportDataType:Int =0
    private var yearList = ArrayList<XAxisValueModel>()
    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()

    companion object {
        fun newInstance(label: String,sportDataType :Int): HeartRtOrTempYearFragment {
            val fragment = HeartRtOrTempYearFragment()
            val args = Bundle()
            fragment.setLabel(label)
            args.putInt(Constants.FRAGMENT_ARG_TYPE, sportDataType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_heart_rate_year
    }

    override fun initBindView() {
        yearBinding.selectDateLayout.dateSelect.text = DateUtil.getYear(System.currentTimeMillis())
        yearBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        yearBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        chatBar = yearBinding.yearChartBar
        updateUIByType()
    }

    private fun updateUIByType(){

        when(sportDataType){

            HeatRtOrTempActivity.heartRateType -> {
                yearBinding.sportDescribe.text ="????????????(???/??????)"
            }
            HeatRtOrTempActivity.temperatureType ->{
                yearBinding.sportDescribe.text ="????????????(?????????)"
            }
        }

    }

    private fun observer(){

        heartRateViewModel.heartRateData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null){
                    updateSportData(it.list)
                    yearBinding.sportDescribe.text ="????????????(???/??????)"
                    yearBinding.sportCount.text="" +it.info.averageHeartRate
                }

            }
            onAppError = { msg, errorCode ->

            }

            onAppComplete ={

            }

        }

        heartRateViewModel.bodyTemperatureData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null){
                    updateBodyTempSportData(it.list)
                    yearBinding.sportDescribe.text ="????????????(?????????)"
                    yearBinding.sportCount.text="" +it.info.averageBodyTemperature
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
        requestData()
    }

    private fun requestData(){
        when(sportDataType){
            HeatRtOrTempActivity.heartRateType -> {
                heartRateViewModel.getHeartRateData(DateUtil.getTimeStampYMD(selectTimeStamp),
                    Constants.monthlyType
                )
            }
            HeatRtOrTempActivity.temperatureType ->{
                heartRateViewModel.getBodyTemperatureData(DateUtil.getTimeStampYMD(selectTimeStamp),
                    Constants.monthlyType
                )
            }
        }
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
                    if(yearList!=null && yearList.size>value.toInt()){
                        var time =""
                        for(item in yearList){
                            if(item.id.toFloat() ==value){
                                time = yearList[value.toInt()].time
                                break
                            }
                        }
                        return time
                    }else{
                        return ""
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


    private fun getSportEntry(sourceData :List<HeartRateModel>): List<BarEntry>{
        val values = java.util.ArrayList<BarEntry>()
        yearList.clear()
        for ( i in sourceData.indices){
            sourceData[i].id= i
            values.add(BarEntry(i.toFloat(), sourceData[i].averageHeartRate.toFloat()))
            yearList.add(XAxisValueModel(i,"" + sourceData[i].month +"???") )
        }
        return values
    }

    private fun getBodySportEntry(sourceData :List<BodyTemperatureModel>): List<BarEntry>{
        val values = java.util.ArrayList<BarEntry>()
        yearList.clear()
        for ( i in sourceData.indices){
            sourceData[i].id= i
            values.add(BarEntry(i.toFloat(), sourceData[i].averageBodyTemperature.toFloat()))
            yearList.add(XAxisValueModel(i,"" + sourceData[i].month +"???"))
        }
        return values
    }

    private fun updateSportData(data : List<HeartRateModel>){
        val entryList =getSportEntry(data)
        setData(entryList)
    }

    private fun updateBodyTempSportData(data : List<BodyTemperatureModel>){
        val entryList =getBodySportEntry(data)
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
                set1 = BarDataSet(values, "The year")
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
                    selectTimeStamp =DateUtil.getLastYearTimeStamp(selectTimeStamp)
                    yearBinding.selectDateLayout.dateSelect.text =  DateUtil.getYear(selectTimeStamp)
                    requestData()
                }else{
                    UToast.showShortToast("???????????????")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    var nextYear =DateUtil.getNextYearTimeStamp(selectTimeStamp)
                    if(nextYear<System.currentTimeMillis()){
                        selectTimeStamp = nextYear
                        yearBinding.selectDateLayout.dateSelect.text =  DateUtil.getYear(selectTimeStamp)
                        requestData()
                    }else{
                        UToast.showShortToast("????????????????????????")
                    }
                }else{
                    UToast.showShortToast("???????????????")
                }
            }
        }
    }

}