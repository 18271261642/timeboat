package net.sgztech.timeboat.ui.fragment

import android.graphics.Color
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
import net.sgztech.timeboat.databinding.FragmentBloodOxWeekBinding
import net.sgztech.timeboat.provide.dataModel.BloodOxygenModel
import net.sgztech.timeboat.provide.dataModel.TotalStepModel
import net.sgztech.timeboat.provide.viewModel.BloodOxViewModel
import net.sgztech.timeboat.ui.activity.WalkDataActivity
import net.sgztech.timeboat.ui.chart.BloodOxFormatter
import net.sgztech.timeboat.ui.chart.CornerBarChart
import net.sgztech.timeboat.ui.chart.SportDataFormatter
import java.text.SimpleDateFormat

class BloodOxWeekFragment : BaseFragment() {
    private val bloodOxViewModel : BloodOxViewModel by viewModels()
    private val weekBinding : FragmentBloodOxWeekBinding by viewBinding()
    private  lateinit var xAxis : XAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : CornerBarChart
    private var weekList = ArrayList<BloodOxygenModel>()
    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    companion object{
        fun newInstance(label: String): BloodOxWeekFragment {
            val fragment = BloodOxWeekFragment()
            fragment.setLabel(label)
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return  R.layout.fragment_blood_ox_week
    }

    override fun initBindView() {
        weekBinding.selectDateLayout.dateSelect.text = "本周"
        weekBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        weekBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
        chatBar =weekBinding.weekBar
    }

    private fun observer(){

        bloodOxViewModel.bloodOxData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null&&it.list!=null){
                    updateSportData(it.list )
                    weekBinding.averageBloodOxCount.text = "" + it.info.averageBloodOxygen
//                    setData()


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
        bloodOxViewModel.getBloodOxData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.weeklyType)
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

        yAxis.valueFormatter = BloodOxFormatter()


    }


    private fun getSportEntry(sourceData :List<BloodOxygenModel>): List<BarEntry>{
        val values = ArrayList<BarEntry>()

        for ( i in sourceData.indices){

            sourceData[i].id= i
            values.add(BarEntry(i.toFloat(), sourceData[i].averageBloodOxygen.toFloat()))

        }
        if(sourceData.isNotEmpty()){
            weekList = sourceData as ArrayList<BloodOxygenModel>
        }


        return values
    }

    private fun updateSportData(data : List<BloodOxygenModel>){
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
                    bloodOxViewModel.getBloodOxData(DateUtil.getTimeStampYMD(selectTimeStamp),
                        Constants.weeklyType
                    )
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    val currentWeek =DateUtil.getWeek(currentTimeStamp)
                    val selectWeek = DateUtil.getWeek(selectTimeStamp)
                    if(currentWeek ==selectWeek){
                        UToast.showShortToast("只能查看历史数据")
                    }else{
                        selectTimeStamp += Constants.oneWeek
                        val selectNextWeek = DateUtil.getWeek(selectTimeStamp)
                        if(selectNextWeek==currentWeek){
                            weekBinding.selectDateLayout.dateSelect.text = "本周"
                        }else{
                            weekBinding.selectDateLayout.dateSelect.text = DateUtil.getWeekString(selectTimeStamp)
                        }
                        bloodOxViewModel.getBloodOxData(DateUtil.getTimeStampYMD(selectTimeStamp), Constants.weeklyType)
                    }
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }
        }

    }

}