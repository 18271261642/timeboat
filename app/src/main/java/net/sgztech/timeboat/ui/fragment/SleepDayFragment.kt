package net.sgztech.timeboat.ui.fragment

import android.graphics.Color
import android.util.SparseArray
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.device.ui.baseUi.baseFragment.BaseFragment
import com.device.ui.viewBinding.viewBinding
import com.device.ui.viewModel.common.vmObserver
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.stockChart.charts.TimeXAxis
import com.github.mikephil.charting.utils.CommonUtil
import com.imlaidian.utilslibrary.utils.DateUtil
import com.imlaidian.utilslibrary.utils.StringsUtils
import com.imlaidian.utilslibrary.utils.UToast
import com.tencent.mm.opensdk.utils.Log
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.databinding.FragmentSleepDayBinding
import net.sgztech.timeboat.provide.dataModel.SleepDailyListBean
import net.sgztech.timeboat.provide.dataModel.SleepDailyModel
import net.sgztech.timeboat.provide.viewModel.SleepDailyViewModel
import net.sgztech.timeboat.ui.activity.WalkDataActivity
import net.sgztech.timeboat.ui.chart.SportBarChart
import net.sgztech.timeboat.ui.repository.SleepDailyRepository
import java.util.*

class SleepDayFragment :BaseFragment(){
    private val TAG =SleepDayFragment::class.java.simpleName
    private val dayBinding : FragmentSleepDayBinding by viewBinding()
    private val sleepDailyViewModel : SleepDailyViewModel by viewModels()
    private  lateinit var xAxis : TimeXAxis
    private  lateinit var yAxis : YAxis
    private  lateinit var  chatBar : SportBarChart
    private var xLabels = SparseArray<String>() //X轴刻度label
    private  val  xUnitValue  = 1
    private val maxXAxisValue = 60*16

    private var currentTimeStamp = 0L
    private var selectTimeStamp = System.currentTimeMillis()
    companion object{
        fun newInstance(label: String ): SleepDayFragment {
            val fragment = SleepDayFragment()
            fragment.setLabel(label)
            return fragment
        }
        val  deepSleepHigh= 50f
        val  lightSleepHigh= 60f
        val  soberSleepHigh= 60f

        // 深度睡眠
        val  deepSleepModelType = 1
        // 浅睡眠
        val  lightSleepModelType = 2
        //清醒
        val  soberModelType =3
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_sleep_day
    }

    override fun initBindView() {
        dayBinding.selectDateLayout.dateSelect.text = DateUtil.getCurrentTime()
        chatBar =dayBinding.sleepDayChart
        dayBinding.selectDateLayout.leftArrowDate.setOnClickListener(this)
        dayBinding.selectDateLayout.rightArrowDate.setOnClickListener(this)
    }

    private fun observer(){

        sleepDailyViewModel.sleepDailyData.vmObserver(this){
            onAppLoading = {

            }
            onAppSuccess = {
                if(it!=null&&it.list!=null){
                    updateSportData(it)
                    dayBinding.soberSleepTime.text = changeTimeFormat(it.info.totalSoberMinute)
                    dayBinding.deepSleepTime.text = changeTimeFormat(it.info.totalDeepSleepMinute)
                    dayBinding.lightSleepTime.text = changeTimeFormat(it.info.totalLightSleepMinute)
                    dayBinding.averageSleepTime.text= changeTimeFormat(it.info.totalMinute)
                }else{
                    if (chatBar.data != null && chatBar.data.dataSetCount > 0) {
                        chatBar.clearValues()
                        chatBar.invalidate()
                    }

                    dayBinding.soberSleepTime.text = "--"
                    dayBinding.deepSleepTime.text = "--"
                    dayBinding.lightSleepTime.text = "--"
                    dayBinding.averageSleepTime.text= "--"

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
        initChar()
    }


    private fun initChar(){
        setUpXAxis()
        setUpYAxis()
        chatBar.legend.isEnabled = false
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
        chatBar.setNoDataText("没有睡眠数据");//没有数据时显示的文字
        //没有数据时显示文字的颜色
        chatBar.setNoDataTextColor(Color.RED);//没有数据时显示文字的颜色
        // force pinch zoom along both axis
        chatBar.setPinchZoom(false)
        //设置 chart 边框线的颜色。
        chatBar.setBorderColor(Color.WHITE);
        //设置 chart 边界线的宽度，单位 dp。
        chatBar.setBorderWidth(3f);
        chatBar.axisRight.isEnabled = false

        chatBar.invalidate()
        observer()
        selectTimeStamp = System.currentTimeMillis()
        sleepDailyViewModel.getSleepDailyData(DateUtil.getTimeStampYMD(selectTimeStamp) )
//        testSortData()
//        setData(7 ,40.toFloat())
    }

    private fun setUpXAxis(){
        xAxis = chatBar.xAxis as TimeXAxis
        //x轴刻度值显示在底部
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false)
//        xAxisLine.enableGridDashedLine(10f, 10f, 0f)
//        xAxisLine.setGridLineWidth(0.7f)
        xAxis.axisMaximum = maxXAxisValue.toFloat()
        xAxis.axisMinimum = 0f
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
        xAxis.setAvoidFirstLastClipping(true)
//        xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase): String {
//
//                return  SimpleDateFormat("MM.dd").format(mBeginTime)
//
//            }
//        }

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


        yAxis.setLabelCount(7, true)
        yAxis.textColor = R.color.y_axis_color
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        //设置y轴上每个点对应的线
        yAxis.setDrawGridLines(true)
        //是否显示Y轴刻度
        yAxis.setDrawLabels(true)
        //是否绘制轴线
        yAxis.setDrawAxisLine(false);
        yAxis.axisLineColor = Color.BLACK


    }


    fun getXLabels(): SparseArray<String> {
        if (xLabels.size() == 0) {
            xLabels.put(0, "18:00")
//            xLabels.put(60*2, "20:00")
            xLabels.put(60*3, "21:00")
            xLabels.put(60*6, "0:00")
            xLabels.put(60*9, "3:00")
//            xLabels.put(60*10, "4:00")
            xLabels.put(60*12, "6:00")
//            xLabels.put(60*14, "8:00")
            xLabels.put(maxXAxisValue, "10:00")
        }
        return xLabels
    }


    /**
     * 是否显示坐标轴label
     *
     * @param isShow
     */
    private fun setShowLabels(isShow: Boolean) {
        chatBar.axisLeft.setDrawLabels(isShow)
        chatBar.axisRight.setDrawLabels(isShow)
        chatBar.xAxis.setDrawLabels(isShow)
    }

    private fun changeDateToIdList(startTime :String ,totalMinute :Int):ArrayList<Int>{
        var idList = ArrayList<Int>()
//        val startDate =StringsUtils.toDate(startTime)
//        Log.d(TAG, "startTime =" +startTime  + ",totalMinute=" +totalMinute + ",startDate="+ startDate)
//        val calendar = Calendar.getInstance()
//        calendar.time = startDate
//        val startHour = calendar[Calendar.HOUR_OF_DAY]
//        val startMinute = calendar[Calendar.MINUTE]
        try{
            var list = startTime.split(":".toRegex())
            val startHour = list[0].toInt()
            val startMinute = list[1].toInt()

            // 睡眠 数据从20：00 开始
            var startId =  if(startHour>=18){
                (startHour-18)*60 + startMinute
            }else{
                (startHour + 6)*60 + startMinute
            }

            var endId =  startId + totalMinute


            for(index in startId..endId step xUnitValue  ){
                idList.add(index)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


        return idList
    }


    private fun testSortData(){
        val sourceData = SleepDailyListBean()
        val deepList = ArrayList<SleepDailyModel>()
        val lightList = ArrayList<SleepDailyModel>()
        val soberList = ArrayList<SleepDailyModel>()
        for(i in 0..5){
            var model = SleepDailyModel()
            var timeStamp = DateUtil.getCurDayEndTimestamp(System.currentTimeMillis())
            var tl =timeStamp - (6-i)*60*60*1000
            model.time= DateUtil.getTimestampHHMM(tl)
            model.duration= 59
            deepList.add(model)
        }

        for(i in 0..5){
            var model = SleepDailyModel()
            var timeStamp = DateUtil.getCurDayEndTimestamp(System.currentTimeMillis())
            var tl =timeStamp +i*60*60*1000
            model.time= DateUtil.getTimestampHHMM(tl)
            model.duration= 59
            lightList.add(model)
        }

        for(i in 6..9){
            var model = SleepDailyModel()
            var timeStamp = DateUtil.getCurDayEndTimestamp(System.currentTimeMillis())
            var tl =timeStamp +i*60*60*1000
            model.time= DateUtil.getTimestampHHMM(tl)
            model.duration= 59
            soberList.add(model)
        }

        sourceData.lightSleep=lightList
        sourceData.soberSleep =soberList
        sourceData.deepSleep =deepList
//        var map =getSportEntry(sourceData)
//        drawMulBarChart(map)

        updateSportData(sourceData)
    }


    private fun getSportEntry(sourceData :SleepDailyListBean): MutableMap<Int, MutableList<BarEntry>>{
//        sourceData.deepSleep.clear()
//        sourceData.lightSleep.clear()
//        sourceData.soberSleep.clear()
        val deepSleepValues  : MutableList<BarEntry> = mutableListOf()
        val lightSleepValues : MutableList<BarEntry> = mutableListOf()
        val soberSleepValues : MutableList<BarEntry> = mutableListOf()
        var sleepMap :MutableMap<Int, MutableList<BarEntry> > = HashMap()
        //睡眠状态,0-清醒/1-浅睡/2-深睡
        for(item in sourceData.list ){
            when(item.sleepState){
                 0 -> sourceData.soberSleep.add(item)
                 1 -> sourceData.lightSleep.add(item)
                 2-> sourceData.deepSleep.add(item)
            }
        }
        for (item  in sourceData.deepSleep){
             addEntry(item ,deepSleepValues,deepSleepModelType)
        }

        for (item  in sourceData.lightSleep){
            addEntry(item ,lightSleepValues ,lightSleepModelType)
        }

        for (item  in sourceData.soberSleep){
            addEntry(item ,soberSleepValues,soberModelType)
        }

        sleepMap[deepSleepModelType] = deepSleepValues

        sleepMap[lightSleepModelType] = lightSleepValues
        sleepMap[soberModelType] = soberSleepValues

        return sleepMap
    }

    private fun addEntry(item :SleepDailyModel, dataSet :MutableList<BarEntry>, type :Int ){

        val idList =  changeDateToIdList(item.time , item.duration)
        for(id in idList){
            when(type){
                // 深度睡眠
                deepSleepModelType ->{
                    dataSet.add(BarEntry(id.toFloat(), deepSleepHigh))
                }
                // 浅睡眠
                lightSleepModelType ->{
                    dataSet.add(BarEntry(id.toFloat(), lightSleepHigh))
                }
                //清醒
                soberModelType  -> {
                    dataSet.add(BarEntry(id.toFloat(), soberSleepHigh))
                }
            }

        }
    }

    private fun updateSportData(sourceData :SleepDailyListBean){
       val dataMap = getSportEntry(sourceData)
        drawMulBarChart(dataMap)
    }

    private fun drawMulBarChart(dataMap :MutableMap<Int, MutableList<BarEntry>>){
        var deepSleepValues = dataMap[deepSleepModelType]
        val lightSleepValues = dataMap[lightSleepModelType]
        val soberSleepValues = dataMap[soberModelType]
        val setDeep: BarDataSet
        val setLight: BarDataSet
        val setSober: BarDataSet

        if((deepSleepValues!=null&&deepSleepValues.isNotEmpty()) ||
            (lightSleepValues!=null&&lightSleepValues.isNotEmpty())   ||
            (soberSleepValues!=null&&soberSleepValues.isNotEmpty())
        ){
            if (chatBar.data != null && chatBar.data.dataSetCount > 0) {
                if(chatBar.data.getDataSetByIndex(0)!=null&&deepSleepValues!=null&&deepSleepValues.isNotEmpty()){
                    setDeep = chatBar.data.getDataSetByIndex(0) as BarDataSet
                    setDeep.values = deepSleepValues
                }

                if(chatBar.data.getDataSetByIndex(1) !=null&&lightSleepValues!=null&&lightSleepValues.isNotEmpty()){
                    setLight = chatBar.data.getDataSetByIndex(1) as BarDataSet
                    setLight.values = lightSleepValues
                }

                if(chatBar.data.getDataSetByIndex(2)!=null&&soberSleepValues!=null&&soberSleepValues.isNotEmpty()){
                    setSober = chatBar.data.getDataSetByIndex(2) as BarDataSet
                    setSober.values = soberSleepValues
                }

                chatBar.data.notifyDataChanged()
                chatBar.notifyDataSetChanged()
                chatBar.invalidate()

            } else {
                setDeep = BarDataSet(deepSleepValues, "深睡")
                setXLabels(getXLabels())
                setShowLabels(true)
                setDeep.setDrawIcons(false)
                setDeep.setDrawValues(false)
//            set1.color = R.color.orange_end
                setDeep.color = ContextCompat.getColor(mContext, R.color.deep_sleep)
//            set1.increasingColor = ContextCompat.getColor(mContext, R.color.orange_end)
//            set1.increasingPaintStyle = Paint.Style.FILL
                setDeep.barShadowColor =ContextCompat.getColor(mContext, R.color.deep_sleep)
                setDeep.barBorderColor =ContextCompat.getColor(mContext, R.color.deep_sleep)

                setLight = BarDataSet(lightSleepValues, "浅睡")
                setLight.setDrawIcons(false)
                setLight.setDrawValues(false)
//            set1.color = R.color.orange_end
                setLight.color = ContextCompat.getColor(mContext, R.color.light_sleep)

                setSober = BarDataSet(soberSleepValues, "清醒")
                setSober.setDrawIcons(false)
                setSober.setDrawValues(false)
//            set1.color = R.color.orange_end
                setSober.color = ContextCompat.getColor(mContext, R.color.sober_sleep)

                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(setDeep)
                dataSets.add(setLight)
                dataSets.add(setSober)
                val data = BarData(dataSets)
                data.setValueTextSize(10f)
//            data.setValueTypeface(tfLight)
                data.barWidth = 1f
//            chatBar.setData(data)
                //下面方法需在填充数据后调用
                xAxis.setXLabels(getXLabels())

                xAxis.setLabelCount(getXLabels().size(), false)
                setShowLabels(true)
                chatBar.setViewPortOffsets(
                    CommonUtil.dip2px(mContext, 30f).toFloat(),
                    CommonUtil.dip2px(mContext, 30f).toFloat(),
                    CommonUtil.dip2px(mContext, 30f).toFloat(),
                    CommonUtil.dip2px(mContext, 30f).toFloat()
                )
                // set data
                chatBar.data = data
                chatBar.setVisibleXRange(maxXAxisValue.toFloat(), maxXAxisValue.toFloat())
                //moveViewTo(...) 方法会自动调用 invalidate()
                // redraw
                var size =0
                if(deepSleepValues!=null){
                    size = deepSleepValues.size
                }
                if(lightSleepValues!=null){
                    size+=lightSleepValues.size
                }

                if(soberSleepValues!=null){
                    size+=soberSleepValues.size
                }

                chatBar.moveViewToX((size + - 1).toFloat())
            }
        }else{
            if (chatBar.data != null && chatBar.data.dataSetCount > 0) {
                chatBar.clearValues()
                chatBar.invalidate()
            }
        }


    }

    private fun setData(count: Int, range: Float) {
        val start = 1f
        val values = ArrayList<BarEntry>()
        var i = start.toFloat()
        while (i < start + count) {
            val data = (Math.random() * (range + 1)).toFloat()
            values.add(BarEntry(i, data))
            i++
        }

//        val start = 0f
//        val values = ArrayList<BarEntry>()
//        var i = start.toFloat()
//        val oneDay = 24*60*60*1000
//        while (i < start + count) {
//            val data = (Math.random() * (range + 1)).toFloat()
//            val timeStamp =(System.currentTimeMillis() -i*oneDay)/1000
//            values.add(BarEntry(timeStamp.toFloat(), data,R.drawable.fade_red))
//            i++
//        }

        drawBarChart(values)

    }

    fun drawBarChart(values:ArrayList<BarEntry>){
        val set1: BarDataSet
        if (chatBar.data != null && chatBar.data.dataSetCount > 0) {
            set1 = chatBar.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chatBar.data.notifyDataChanged()
            chatBar.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "浅睡")
            setXLabels(getXLabels())
            setShowLabels(true)
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
            data.barWidth = 1f

            chatBar.setData(data)


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
                    selectTimeStamp -= Constants.oneDay
                    dayBinding.selectDateLayout.dateSelect.text = DateUtil.getTimeStampYMD(selectTimeStamp)
                    sleepDailyViewModel.getSleepDailyData(DateUtil.getTimeStampYMD(selectTimeStamp))
                }else{
                    UToast.showShortToast("请稍候点击")
                }
            }

            R.id.right_arrow_date->{
                if(System.currentTimeMillis() -currentTimeStamp  >500){
                    currentTimeStamp  =System.currentTimeMillis()
                    if((selectTimeStamp + Constants.oneDay)<System.currentTimeMillis()){
                        selectTimeStamp += Constants.oneDay
                        dayBinding.selectDateLayout.dateSelect.text = DateUtil.getTimeStampYMD(selectTimeStamp)
                        sleepDailyViewModel.getSleepDailyData(DateUtil.getTimeStampYMD(selectTimeStamp))
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