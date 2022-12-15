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
import net.sgztech.timeboat.databinding.FragmentAllSportYearBinding
import net.sgztech.timeboat.provide.dataModel.AllSportBean
import net.sgztech.timeboat.provide.dataModel.AllSportStepModel
import net.sgztech.timeboat.provide.viewModel.AllSportViewModel
import net.sgztech.timeboat.ui.activity.SingleSportDataListActivity
import net.sgztech.timeboat.ui.adapter.AllSportListAdapter
import net.sgztech.timeboat.ui.chart.CornerBarChart
import net.sgztech.timeboat.ui.chart.FloatToIntFormatter

class AllSportYearFragment :BaseFragment() {
    private  val yearBinding : FragmentAllSportYearBinding by viewBinding()
    private val  yearViewModel : AllSportViewModel by viewModels()
    private  lateinit var xAxis : XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var yearList = ArrayList<AllSportStepModel>()

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
        fun newInstance(label: String ): AllSportYearFragment {
            val fragment = AllSportYearFragment()
            fragment.setLabel(label)
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return  R.layout.fragment_all_sport_year
    }

    override fun initBindView() {
        yearBinding.selectDateLayout.dateSelect.text = DateUtil.getYear(System.currentTimeMillis())
        yearBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        yearBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        chatBar =yearBinding.sportYearBar
        configureResultList()
    }

    private fun observer(){

        yearViewModel.allSportData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null&&it.monthList.size>0){
                    updateSportData(it.monthList )
                    updateSportInfo(it)
                    startDate = it.beginDate
                    endDate = it.endDate
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
        yearBinding.distanceCount.text= StringsUtils.getFormatDecimal(bean.totalDistance.toDouble() /1000 ,2)
        yearBinding.calorieCount.text= StringsUtils.getFormatDecimal(bean.totalCalorie.toDouble() /1000 ,2)
        yearBinding.spaceTimeCount.text= "" +bean.totalTime/60
        yearBinding.timesCount.text= "" +bean.totalCount
    }

    private fun configureResultList() {
        with(yearBinding.sportList) {
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
        chatBar.legend.isEnabled = true
        chatBar.invalidate()
//        testDate(7 ,40.toFloat())
        observer()
        selectTimeStamp = System.currentTimeMillis()
        requestData()
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
                    if(yearList!=null && yearList.size>value.toInt()){
                        var time =0
                        for(item in yearList){
                            if(item.id.toFloat() ==value){
                                time = yearList[value.toInt()].month
                                break
                            }
                        }
                        return "" +time + "月"
                    }else{
                        return ""
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

        yAxis.valueFormatter = FloatToIntFormatter()

    }
    private fun setUpLegend(){
        val l: Legend = chatBar.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.EMPTY
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
        yearList = sourceData as ArrayList<AllSportStepModel>

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
                set1 = BarDataSet(values, "时长(分钟)")
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
        yearViewModel.getAllSportData(
            DateUtil.getTimeStampYMD(selectTimeStamp),
            Constants.yearlyType
        )
    }


    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.left_arrow_date ->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    selectTimeStamp = DateUtil.getLastYearTimeStamp(selectTimeStamp)
                    yearBinding.selectDateLayout.dateSelect.text = DateUtil.getYear(selectTimeStamp)
                    requestData()
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    var nextYear =DateUtil.getNextYearTimeStamp(selectTimeStamp)
                    if(nextYear < System.currentTimeMillis()){
                        selectTimeStamp = nextYear
                        yearBinding.selectDateLayout.dateSelect.text = DateUtil.getYear(selectTimeStamp)
                        requestData()
                    }else{
                        UToast.showShortToast("只能查看历史数据")
                    }
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }
        }

    }
}