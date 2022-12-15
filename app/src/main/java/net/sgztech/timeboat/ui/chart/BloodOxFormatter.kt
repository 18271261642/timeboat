package net.sgztech.timeboat.ui.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import com.imlaidian.utilslibrary.utils.StringsUtils

class BloodOxFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {

        val formatterValue =StringsUtils.getFormatDecimal(value.toDouble(),2)
        return "$formatterValue%"
    }
}