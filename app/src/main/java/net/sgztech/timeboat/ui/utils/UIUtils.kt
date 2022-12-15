package net.sgztech.timeboat.ui.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.device.ui.ext.ActivityMessenger
import com.device.ui.ext.startActivityForResult
import com.device.ui.extension.onTabSelected
import com.google.android.material.tabs.TabLayout
import com.imlaidian.utilslibrary.UtilsApplication
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.R
import net.sgztech.timeboat.TimeBoatApplication
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.provide.dataModel.BottomResourceData
import net.sgztech.timeboat.ui.activity.MainActivity
import java.io.File


object UIUtils {
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    @JvmStatic
    fun backgroundAlpha(aty: Activity, bgAlpha: Float) {
        val lp = aty.window.attributes
        lp.alpha = bgAlpha //0.0-1.0
        aty.window.attributes = lp
    }

    fun hideSoftInput(cxt: Context, v: View) {
        val imm = cxt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun showSoftInput(cxt: Context, v: View) {
        val imm = cxt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, 0)
//        cxt.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


    }

    fun  installApk(cxt: Context ,path :String){
        val filePath =  File(Environment.getExternalStorageDirectory(), "terminal/app/");
        val newFile =  File(filePath, "upgrade.apk");
        installApk(cxt ,newFile);
    }


    private fun installApk(cxt: Context ,newFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri =
                FileProvider.getUriForFile(cxt, "net.sgztech.timeboat.fileprovider", newFile)
            Log.d("Test", "uri =$uri")
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            val resolveInfoList: List<ResolveInfo> = cxt.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resolveInfoList) {
                val packageName: String = resolveInfo.activityInfo.packageName
                cxt.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(
                Uri.parse("file://" + newFile.getAbsolutePath()),
                "application/vnd.android.package-archive"
            )
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        cxt.startActivity(intent)
    }

    fun setHomeTablayoutCustomView(mContext: Context, tlTablayout: TabLayout, bottomList: ArrayList<BottomResourceData>) {
        for (i in 0 until tlTablayout.tabCount) {
            val itemView: View =
                LayoutInflater.from(mContext).inflate(R.layout.tab_home_item, tlTablayout, false)
            val txtView = itemView.findViewById<View>(R.id.tv_tab_title) as TextView
            txtView.text = bottomList[i].getTitle()
            val ivImg = itemView.findViewById<View>(R.id.iv_img) as ImageView

            //默认选中第一项
            if (i == 0) {
                txtView.setTextColor(mContext.getColor(R.color.colour_orange))
                ivImg.setImageResource(bottomList[i].getResource())
            } else {
                txtView.setTextColor(mContext.getColor(R.color.grey_c8))
                ivImg.setImageResource(bottomList[i].getUnCheckResource())
            }
            tlTablayout.getTabAt(i)!!.customView = itemView
        }

        //设置监听
        tlTablayout.onTabSelected {
            onTabSelected {
                for (i in 0 until tlTablayout.tabCount) {
                    val itemView: View = tlTablayout.getTabAt(i)!!.customView!!
                    val txtView = itemView.findViewById<View>(R.id.tv_tab_title) as TextView
                    val ivImg = itemView.findViewById<View>(R.id.iv_img) as ImageView
                    if (i != tlTablayout.selectedTabPosition) {
                        txtView.setTextColor(mContext.getColor(R.color.grey_c8))
                        ivImg.setImageResource(bottomList[i].getUnCheckResource())
                    }
                }
                customView?.let {
                    (it.findViewById<View>(R.id.tv_tab_title) as TextView).setTextColor(
                        mContext.getColor(
                            R.color.colour_orange
                        )
                    )
                    (it.findViewById<View>(R.id.iv_img) as ImageView).setImageResource(bottomList[tlTablayout.selectedTabPosition].getResource())
                }
            }
        }
    }


    private fun showDialog( context: Context ,title :String  ,sureButton :String  ,cancelButton:String){
        AlertDialog.Builder(context).apply {
            setMessage(title)
            setPositiveButton(sureButton) { dialog, which ->
                dialog.dismiss()
            }
            setNegativeButton(cancelButton) { dialog, which ->
                dialog.dismiss()
            }
            create()
        }.show()
    }


    fun  isNetworkAvailable(): Boolean {
        val context = UtilsApplication.getInstance().applicationContext
       return isNetworkAvailable(context)
    }

    fun isNetworkAvailable(ctx: Context?): Boolean {
        var result = true
        if (null != ctx) {
            val connectivityManager =
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if(connectivityManager!=null){
                val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (networkCapabilities == null) {
                    result =false
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    LogUtil.d("UIUtils","当前使用移动网络")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    LogUtil.d("UIUtils","当前使用WIFI网络")
                }else{
                    result =false
                }
            }else{
                result =false
            }

        }else{
            result =false
        }

        return result
    }

}