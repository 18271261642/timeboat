package net.sgztech.timeboat.managerUtlis

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import com.imlaidian.utilslibrary.UtilsApplication
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import net.sgztech.timeboat.R
import net.sgztech.timeboat.config.Constants

class ThirdWxLoginManager {

    lateinit var mWXAPI: IWXAPI
    var listen : WechatCodeListen?=null

    companion object {
        val instance: ThirdWxLoginManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ThirdWxLoginManager() }
    }

    init {
        getWXApi()
    }
    fun getWXApi(){
        val ctx: Context = UtilsApplication.getInstance().applicationContext
        mWXAPI = WXAPIFactory.createWXAPI(ctx, Constants.WEIXIN_APPID, true)

    }

    fun registerToWeiXin() {
        mWXAPI.registerApp(Constants.WEIXIN_APPID)
    }

    fun getApi():IWXAPI{
        return mWXAPI
    }


    fun login() {
        val  ctx: Context = UtilsApplication.getInstance().applicationContext
        if (!isWXAPPInstalled(ctx)) {
            Toast.makeText(ctx, ctx.getString(R.string.login_wx_uninstall), Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (mWXAPI.wxAppSupportAPI< Build.TIMELINE_SUPPORTED_SDK_INT) {
            Toast.makeText(ctx, ctx.getString(R.string.login_wx_old_version), Toast.LENGTH_SHORT)
                .show()
            return
        }
        val req = SendAuth.Req()
        req.scope = Constants.WEIXIN_SCOPE
        req.state = "none"
        //req.openId = "";
        mWXAPI.sendReq(req)
    }

    fun isWXAPPInstalled(ctx: Context): Boolean {
        if (mWXAPI == null) {
            return false
        }
        var result: Boolean = mWXAPI!!.isWXAppInstalled()
        if (result) {
            val packageManager = ctx.packageManager
            if (packageManager != null) {
                try {
                    val info = packageManager.getApplicationInfo(
                        "com.tencent.mm", PackageManager.GET_META_DATA
                    )
                    if (info != null) {
                        result = true
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    result = false
                }
            }
        }
        return result
    }

    fun loginUseWechat(code: String?) {


        if (code != null && code.length > 0) {
            if(listen!=null){
                listen!!.getCode(code)
            }
        }
    }

    fun registerWxListen(wxlisten :WechatCodeListen){
        if(listen!=null){
            listen =null
        }
        listen =wxlisten
    }
    fun unRegisterWxListen(){
        listen =null ;
    }

    interface WechatCodeListen{
        fun getCode(code: String?)
    }

}