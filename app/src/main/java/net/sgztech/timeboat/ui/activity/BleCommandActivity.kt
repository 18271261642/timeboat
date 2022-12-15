package net.sgztech.timeboat.ui.activity

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.device.ui.baseUi.baseActivity.BaseActivity
import com.device.ui.viewBinding.viewBinding
import com.imlaidian.utilslibrary.utils.UToast
import net.sgztech.timeboat.util.toHex
import net.sgztech.timeboat.R
import net.sgztech.timeboat.bleCommand.CommandType
import net.sgztech.timeboat.config.Constants.Companion.MAC_Address
import net.sgztech.timeboat.databinding.ActivityBleCommandBinding
import net.sgztech.timeboat.managerUtlis.BleServiceManager

class BleCommandActivity :BaseActivity(){
    val TAG= BleCommandActivity::class.java.simpleName
    val commandBind :ActivityBleCommandBinding by viewBinding()
    val editorCmd :String =""
    var receiveInfo :String=""
    companion object{
        val easyWrite = 1
        val complexWrite = 2
        val synTimeWrite =3
    }
    var writeCheckType =easyWrite ;

    override fun getLayoutId(): Int {
        return  R.layout.activity_ble_command
    }

    override fun initBindView() {

        commandBind.writeBtn.setOnClickListener(this)
        commandBind.readDataBtn.setOnClickListener(this)
        commandBind.cleanDataBtn.setOnClickListener(this)
        commandBind.easyCommand.setOnClickListener(this)
        commandBind.complexCommand.setOnClickListener(this)
        commandBind.synTimeCommand.setOnClickListener(this)
        commandBind.backLayout.backArrow.setOnClickListener(this)
        commandBind.writeCmd.setOnEditorActionListener(object:TextView.OnEditorActionListener{
            override fun onEditorAction(cmdText: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                val cmd = cmdText?.text
                Log.d(TAG, "editor txt="+editorCmd)
                return false
            }
        })
    }

    override fun initData() {
        super.initData()
        val macAddress =intent.getStringExtra(MAC_Address)
        if(macAddress!=null){
            BleServiceManager.instance.setBleDevice(macAddress)
            BleServiceManager.instance.registerNotify(object: BleServiceManager.NotifyInfoListen{
                override fun notify(notify: ByteArray, describe: String) {
                    runOnUiThread {
                        receiveInfo = receiveInfo + notify.toHex() +"\n"
                        commandBind.receiveMsg.text=receiveInfo
                    }
                }
            })
        }else{
            UToast.showShortToast("mac地址为null")
        }
    }

    override fun widgetClick(v: View) {
        super.widgetClick(v)
        when(v.id){
            R.id.write_btn ->{
               when(writeCheckType){
                   easyWrite->{
                       var  writeCmd = commandBind.writeCmd.text.toString()
                       var cmdList = translateInputToCommand(writeCmd)
                       if(cmdList!=null && cmdList.size>1){
                           UToast.showShortToast("简单模式只需要一个模式")
                       }else if(cmdList!=null){
                          var bleCmd = CommandType.buildCommandHeader(cmdList[0].toByte() ,0)
                           BleServiceManager.instance.writeData(bleCmd)
//                           BleServiceManager.instance.connectWriteCharacteristicUuid(bleCmd)
                       }

                   }
                   complexWrite->{
                       var  writeCmd = commandBind.writeCmd.text.toString()
                       var cmdList = translateInputToCommand(writeCmd)
                       if(cmdList!=null){
//                           BleServiceManager.instance.connectWriteCharacteristicUuid(cmdList)
                           BleServiceManager.instance.writeData(cmdList)
                       }else{
                           UToast.showShortToast("输入命令错误，请检查");
                       }
                   }
                   synTimeWrite->{
                       var bleCmd =  CommandType.buildSyncTimeCommand()//SynTimeCommand.synTimeCommandItem()
                       commandBind.writeCmd.setText(bleCmd.toHex())
//                       BleServiceManager.instance.connectWriteCharacteristicUuid(bleCmd)
                       BleServiceManager.instance.writeData(bleCmd)
                   }

               }

            }
            R.id.read_data_btn->{
                BleServiceManager.instance.readData()
            }
            R.id.clean_data_btn->{
                receiveInfo =""
                commandBind.receiveMsg.text=""
            }
            R.id.easy_command->{
                writeCheckType =easyWrite
                commandBind.easyCommand.isChecked =true
                commandBind.complexCommand.isChecked=false
                commandBind.synTimeCommand.isChecked =false
            }
            R.id.complex_command->{
                writeCheckType =complexWrite
                commandBind.easyCommand.isChecked =false
                commandBind.complexCommand.isChecked=true
                commandBind.synTimeCommand.isChecked =false
            }
            R.id.syn_time_command->{
                writeCheckType =synTimeWrite
                commandBind.easyCommand.isChecked =false
                commandBind.complexCommand.isChecked=false
                commandBind.synTimeCommand.isChecked =true
            }
            R.id.back_arrow->{
                finish()
            }
        }
    }

    private fun translateInputToCommand(input: String): ByteArray? {
        if (input == "") {
            UToast.showShortToast("命令不能为空")
            return null
        }
        val commandGroup = input.split(" ").toTypedArray()
        val cmd = ByteArray(commandGroup.size)
        for (i in commandGroup.indices) {
            commandGroup[i] = commandGroup[i].trim { it <= ' ' }
            try {
                cmd[i] = commandGroup[i].toInt(16).toByte()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "commandGroup error:", e)
                UToast.showLongToast("请确认输入格式，第"+ (i + 1) + "输入有误")

            }
        }
        return cmd
    }

    override fun onDestroy() {
        super.onDestroy()
        BleServiceManager.instance.unRegisterNotify()
        BleServiceManager.instance.triggerCharDisconnect()
    }
}