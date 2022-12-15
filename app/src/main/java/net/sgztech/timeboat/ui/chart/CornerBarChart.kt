package net.sgztech.timeboat.ui.chart

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.stockChart.renderer.CornerBarChartRenderer


class CornerBarChart : BarChart {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )


    override fun initMyBarRenderer() { //调用自己的渲染器
        mRenderer =
            CornerBarChartRenderer(
                this,
                mAnimator,
                mViewPortHandler
            )
    }
}