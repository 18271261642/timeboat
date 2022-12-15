package net.sgztech.timeboat.ui.chart

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.stockChart.charts.TimeXAxis
import com.github.mikephil.charting.stockChart.renderer.TimeBarChartRenderer
import com.github.mikephil.charting.stockChart.renderer.TimeXAxisRenderer

class SportBarChart: BarChart {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun initMyBarRenderer() { //调用自己的渲染器
        mRenderer = TimeBarChartRenderer(this, mAnimator, mViewPortHandler)
    }

    override fun initXAxisRenderer() {
        mXAxisRenderer =
            TimeXAxisRenderer(mViewPortHandler, mXAxis as TimeXAxis, mLeftAxisTransformer, this)
    }

    override fun initXAxis() {
        mXAxis = TimeXAxis()
    }

}