package net.sgztech.timeboat.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
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
import net.sgztech.timeboat.databinding.FragmentAllSportWeekBinding
import net.sgztech.timeboat.provide.dataModel.AllSportBean
import net.sgztech.timeboat.provide.dataModel.AllSportStepModel
import net.sgztech.timeboat.provide.viewModel.AllSportViewModel
import net.sgztech.timeboat.ui.activity.SingleSportDataListActivity
import net.sgztech.timeboat.ui.adapter.AllSportListAdapter
import net.sgztech.timeboat.ui.chart.CornerBarChart
import net.sgztech.timeboat.ui.chart.FloatToIntFormatter
import java.text.SimpleDateFormat

class AllSportWeekFragment :BaseFragment(){

    private  val weekBinding : FragmentAllSportWeekBinding by viewBinding()
    private val  weekViewModel : AllSportViewModel by viewModels()
    private  lateinit var xAxis : XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var weekList = ArrayList<AllSportStepModel>()

    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    private var startDate =""
    private var endDate =""
    private val resultsAdapter =
        AllSportListAdapter { result, position ->
                val intent  = Intent(activity , SingleSportDataListActivity::class.java)
                intent.putExtra(Constants.SPORT_TYPE_START_DATE ,startDate )
                intent.putExtra(Constants.SPORT_TYPE_END_DATE ,endDate )
                intent.putExtra(Constants.SPORT_TYPE_NAME ,result.sportName )
                 intent.putExtra(Constants.SPORT_TYPE_VALUE ,result.sportType )
                intent.putExtra(Constants.SPORT_TYPE_ICON_URL ,result.sportIcon)
                startActivity(intent)
        }
    companion object{
        fun newInstance(label: String ): AllSportWeekFragment {
            val fragment = AllSportWeekFragment()
            fragment.setLabel(label)
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return  R.layout.fragment_all_sport_week
    }

    override fun initBindView() {
        weekBinding.selectDateLayout.dateSelect.text = "??????"
        chatBar =weekBinding.sportWeekBar
        weekBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        weekBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        configureResultList()
    }

    private fun observer(){

        weekViewModel.allSportData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null&&it.dayList.size>0){
                    updateSportData(it.dayList )
                    startDate = it.beginDate
                    endDate = it.endDate
                    updateSportInfo(it)
                    resultsAdapter.refreshData(it.sportList)
                }
            }
            onAppError = { msg, errorCode ->

            }

            onAppComplete ={

            }

        }

    }

    private fun updateSportInfo(bean : AllSportBean){
        weekBinding.distanceCount.text= StringsUtils.getFormatDecimal(bean.totalDistance.toDouble() /1000 ,2)
        weekBinding.calorieCount.text= StringsUtils.getFormatDecimal(bean.totalCalorie.toDouble() /1000 ,2)
        weekBinding.spaceTimeCount.text= "" +bean.totalTime/60
        weekBinding.timesCount.text= "" +bean.totalCount
    }

    private fun configureResultList() {
        with(weekBinding.sportList) {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = resultsAdapter
        }

    }

    override fun initData() {
        setUpXAxis()
        setUpYAxis()
        setUpLegend()
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
        chatBar.legend.isEnabled = true
        chatBar.invalidate()
//        testDate(7 ,40.toFloat())
        observer()
        selectTimeStamp = System.currentTimeMillis()
        requestData()
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

        yAxis.valueFormatter = FloatToIntFormatter()


    }

    private fun setUpLegend(){
        val l: Legend = chatBar.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.form = LegendForm.EMPTY
//        l.typeface = tfLight
        l.yOffset = 0f
        l.xOffset = -40f
        l.yEntrySpace = 0f
        l.textSize = 10f

    }

    private fun getSportEntry(sourceData :List<AllSportStepModel>): List<BarEntry>{
        val values = ArrayList<BarEntry>()

        for ( i in sourceData.indices){

            sourceData[i].id= i
            values.add(BarEntry(i.toFloat(), sourceData[i].useTime.toFloat()))

        }
        weekList = sourceData as ArrayList<AllSportStepModel>

        return values
    }

    private fun updateSportData(data : List<AllSportStepModel>){
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
                chatBar.invalidate();
            } else {
                set1 = BarDataSet(values, "??????(??????)")
                set1.setDrawIcons(false)
//            set1.color = R.color.orange_end
                set1.color = ContextCompat.getColor(mContext, R.color.colour_orange)
//            set1.increasingColor = ContextCompat.getColor(mContext, R.color.orange_end)
//            set1.increasingPaintStyle = Paint.Style.FILL
                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(set1)
                val data = BarData(dataSets)
                data.setValueTextSize(10f)
                data.setDrawValues(false)
//            data.setValueTypeface(tfLight)
                data.barWidth = 0.4f

                chatBar.setData(data)
                chatBar.invalidate();

            }
        }else{
            if (chatBar.data != null &&
                chatBar.data.dataSetCount > 0) {
                chatBar.clearValues();
                chatBar.invalidate();
            }
        }


    }
    private fun requestData(){
        weekViewModel.getAllSportData(
            DateUtil.getTimeStampYMD(selectTimeStamp),
            Constants.weeklyType
        )
    }


    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.left_arrow_date ->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    selectTimeStamp -= Constants.oneWeek
                    weekBinding.selectDateLayout.dateSelect.text = DateUtil.getWeekString(selectTimeStamp)
                    weekViewModel.getAllSportData(DateUtil.getTimeStampYMD(selectTimeStamp),
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
                        weekViewModel.getAllSportData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.weeklyType)
                    }
                }else{
                    UToast.showShortToast("???????????????")
                }
            }
        }

    }

}