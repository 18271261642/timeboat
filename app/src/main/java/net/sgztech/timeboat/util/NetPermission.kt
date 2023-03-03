package net.sgztech.timeboat.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import net.sgztech.timeboat.config.Constants
import net.sgztech.timeboat.config.Constants.Companion.PERMISSIONS_ACCESS_COARSE_LOCATION
import net.sgztech.timeboat.managerUtlis.LocationAddressManager


internal fun Activity.checkExternalWritePermission() :Boolean{
    val permissionCheck: Int =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            Constants.PERMISSIONS_REQUEST_STORAGE
        )
        return false
    }else{
        return true
    }
}

internal fun Activity.checkNetWorkPermission() :Boolean {
    val permissionCheck: Int =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
            Constants.PERMISSIONS_NETWORK_STATE
        )
        return false
    }else{
        return true
    }
}

// 动态申请权限
internal fun Activity.initListPermission() {
    val mPermissionList: ArrayList<String> = ArrayList<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 版本大于等于 Android12 时
        // 只包括蓝牙这部分的权限，其余的需要什么权限自己添加
        mPermissionList.add(Manifest.permission.BLUETOOTH_SCAN)
        mPermissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        mPermissionList.add(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        // Android 版本小于 Android12 及以下版本
        mPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (mPermissionList.size > 0) {
        ActivityCompat.requestPermissions(
            this,
            mPermissionList.toArray(arrayOfNulls<String>(0)),
            1001
        )
    }
}


internal fun Activity.checkWiFiPermission() :Boolean{
    val permissionCheck: Int =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
            Constants.PERMISSIONS_WIFI_STATE
        )
        return false
    }else{
        return true
    }
}





internal fun Activity.checkLocationPermission():Boolean{

    val permissionCheck: Int =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//        requestPermissions(
//            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//            Constants.PERMISSIONS_ACCESS_COARSE_LOCATION
//        )
        return false
    }else{
        return true
    }
}


internal fun LocationPermissionGranted(requestCode: Int, grantResults: IntArray) =
    requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED



internal fun Activity.initLocationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // 检查权限状态
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                /**
                 * 用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
                 * 第一次授权失败之后，退出App再次进入时，再此处重新调出允许权限提示框
                 */
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    Constants.PERMISSIONS_ACCESS_FINE_LOCATION
//                )
                Log.d("info:", "-----get--Permissions--success--1-")
            } else {
                /**
                 * 用户未彻底拒绝授予权限
                 * 第一次安装时，调出的允许权限提示框，之后再也不提示
                 */
                Log.d("info:", "-----get--Permissions--success--2-")
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    Constants.PERMISSIONS_ACCESS_COARSE_LOCATION
//                )
            }
        } else {
            LocationAddressManager.instance.getLatLng()
        }
    }else{
        LocationAddressManager.instance.getLatLng()
    }

}
