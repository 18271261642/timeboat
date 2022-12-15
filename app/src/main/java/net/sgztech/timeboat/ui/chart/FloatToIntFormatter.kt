package net.sgztech.timeboat.ui.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import com.imlaidian.utilslibrary.utils.StringsUtils

class FloatToIntFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {

        return StringsUtils.getFormatDecimal(value/60.toDouble(),2)
    }
}