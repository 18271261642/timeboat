package net.sgztech.timeboat.ui.adapter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class AxisValueFormatter : ValueFormatter(){


    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return super.getFormattedValue(value, axis)
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return super.getAxisLabel(value, axis)
    }
}