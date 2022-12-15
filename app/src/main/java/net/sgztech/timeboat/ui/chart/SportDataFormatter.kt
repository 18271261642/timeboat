package net.sgztech.timeboat.ui.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import com.imlaidian.utilslibrary.utils.StringsUtils

class SportDataFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        val data=value/1000

        return  StringsUtils.getFormatDecimal(data.toDouble(),2)
    }
}