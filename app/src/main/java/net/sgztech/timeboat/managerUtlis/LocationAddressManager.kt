package net.sgztech.timeboat.managerUtlis

import android.content.Context
import android.location.*
import android.os.Bundle
import com.imlaidian.utilslibrary.UtilsApplication
import com.imlaidian.utilslibrary.utils.LogUtil
import net.sgztech.timeboat.managerUtlis.StringsUtils.getFormatDecimal

class LocationAddressManager {
    private val TAG = LocationAddressManager::class.java.simpleName

    private lateinit var locationManager: LocationManager
    var latitude = ""
    var longitude = ""
    var altitude = ""

    //当前可用的位置控制器
    private var list: List<String>? = null

    companion object {

        val instance: LocationAddressManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LocationAddressManager()
        }
    }


    private val locationListener: LocationListener = object : LocationListener {
        //位置发生改变时调用
        override fun onLocationChanged(location: Location) {
            val context = UtilsApplication.getInstance().applicationContext
            //解析地址并显示
            val geoCoder = Geocoder(context)
            try {
                latitude = getFormatDecimal(location.latitude, 2)
                longitude = getFormatDecimal(location.longitude, 2)
                altitude = getFormatDecimal(location.altitude, 2)
                LogUtil.d(
                    TAG,
                    "onLocationChanged Latitude=" + location.latitude + "，longitude=" + location.longitude
                )
                val list = geoCoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 2)
                for (i in list.indices) {
                    val address = list[i]
                    LogUtil.d(
                        TAG,
                        "location=" + address.countryName + address.adminArea + address.featureName
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //provider失效时调用
        override fun onProviderDisabled(provider: String) {
            LogUtil.d(TAG, "onProviderDisabled")
        }

        //provider启用时调用
        override fun onProviderEnabled(provider: String) {
            LogUtil.d(TAG, "onProviderEnabled")
        }

        //状态改变时调用
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            LogUtil.d(TAG, "onStatusChanged")
        }
    }


    fun getLatLng() {
        val context = UtilsApplication.getInstance().applicationContext
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //获取当前可用的位置控制器
        list = locationManager.getProviders(true)

        //创建一个Criteria对象
        val criteria = Criteria()
        //设置粗略精确度
        criteria.accuracy = Criteria.ACCURACY_COARSE
        //设置是否需要返回海拔信息
        criteria.isAltitudeRequired = false
        //设置是否需要返回方位信息ß
        criteria.isBearingRequired = false
        //设置是否允许付费服务
        criteria.isCostAllowed = false
        //设置电量消耗等级
        criteria.powerRequirement = Criteria.POWER_LOW
        //设置是否需要返回速度信息
        criteria.isSpeedRequired = false

        //根据设置的Criteria对象，获取最符合此标准的provider对象
        val currentProvider: String?
        val providers = locationManager.getProviders(criteria, true)
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            currentProvider = LocationManager.NETWORK_PROVIDER
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            currentProvider = LocationManager.GPS_PROVIDER
        } else {
            currentProvider = null
//            currentProvider =locationManager.getBestProvider(criteria, true)
        }

        LogUtil.d(TAG, "currentProvider: $currentProvider")

        //根据当前provider对象获取最后一次位置信息
        if (currentProvider != null) {
            val currentLocation: Location? = locationManager.getLastKnownLocation(currentProvider)
            if (currentLocation != null) {
                latitude = getFormatDecimal(currentLocation.latitude, 2)
                longitude = getFormatDecimal(currentLocation.longitude, 2)
                altitude = getFormatDecimal(currentLocation.altitude, 2)
                LogUtil.d(TAG, "Latitude: " + currentLocation.latitude)
                LogUtil.d(TAG, "longitude: " + currentLocation.longitude)
                LogUtil.d(TAG, "altitude: " + currentLocation.altitude)
                locationManager.requestLocationUpdates(currentProvider, 0, 100f, locationListener)

            } else {
                locationManager.requestLocationUpdates(currentProvider, 0, 100f, locationListener)

            }
        } else {
            if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 100f,
                    locationListener
                )
            } else {
                list = locationManager.getProviders(true)
                if (list != null && list!!.size > 0) {
                    locationManager.requestLocationUpdates(
                        list!!.get(0), 0, 100f,
                        locationListener
                    )
                }
            }

        }
        //如果位置信息为null，则请求更新位置信息
    }

}