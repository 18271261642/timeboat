package net.sgztech.timeboat.provide.dataModel

import androidx.annotation.Keep

@Keep
class BottomResourceData(title: String, resouce: Int, uncheckResouce: Int){
    var getTitle: () -> String = { title }
    var getResource:() -> Int = { resouce }
    var getUnCheckResource:() -> Int = { uncheckResouce }
}