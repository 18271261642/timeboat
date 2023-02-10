package net.sgztech.timeboat.ui.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import net.sgztech.timeboat.R

/**
 * Created by Admin
 *Date 2023/2/4
 */
class ShowLoclDescDialog : AppCompatDialog {

    constructor(context: Context) : super (context){

    }

    constructor(context: Context,theme : Int) : super (context,theme){


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_show_local_permission_layout)

    }

}