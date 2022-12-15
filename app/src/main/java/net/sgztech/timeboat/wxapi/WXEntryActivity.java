package net.sgztech.timeboat.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.imlaidian.utilslibrary.utils.LogUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView;
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.managerUtlis.ThirdWxLoginManager;
import java.util.Objects;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private static String TAG = "WXEntryActivity";

//    private IWXAPI api;
//    private MyHandler handler;

//	private static class MyHandler extends Handler {
//		private final WeakReference<WXEntryActivity> wxEntryActivityWeakReference;
//
//		public MyHandler(WXEntryActivity wxEntryActivity){
//			wxEntryActivityWeakReference = new WeakReference<WXEntryActivity>(wxEntryActivity);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			int tag = msg.what;
//			switch (tag) {
//				case NetworkUtil.GET_TOKEN: {
//					Bundle data = msg.getData();
//					JSONObject json = null;
//					try {
//						json = new JSONObject(data.getString("result"));
//						String openId, accessToken, refreshToken, scope;
//						openId = json.getString("openid");
//						accessToken = json.getString("access_token");
//						refreshToken = json.getString("refresh_token");
//						scope = json.getString("scope");
//						Intent intent = new Intent(wxEntryActivityWeakReference.get(), SendToWXActivity.class);
//						intent.putExtra("openId", openId);
//						intent.putExtra("accessToken", accessToken);
//						intent.putExtra("refreshToken", refreshToken);
//						intent.putExtra("scope", scope);
//						wxEntryActivityWeakReference.get().startActivity(intent);
//					} catch (JSONException e) {
//						Log.e(TAG, e.getMessage());
//					}
//				}
//			}
//		}
//	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//		handler = new MyHandler(this);
//
        try {
			ThirdWxLoginManager.Companion.getInstance().getApi().handleIntent(getIntent(), this);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
//		setIntent(intent);
//		try {
//			ThirdWxLoginManager.Companion.getInstance().getApi().handleIntent(intent, this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void onReq(BaseReq req) {
//		switch (req.getType()) {
//		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
////			goToGetMsg();
//			break;
//		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
////		COMMAND_SHOWMESSAGE_FROM_WXΩ	goToShowMsg((ShowMessageFromWX.Req) req);
//			break;
//		default:
//			break;
//		}
//        finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		
		switch (resp.errCode) {
			//用户同意
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;

			//用户取消
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
			//用户拒绝授权
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			result = R.string.errcode_unsupported;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		
//		Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();


		LogUtil.d(TAG ,"onResp type=" +resp.getType());
		if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
//			Toast.makeText(this, getString(result) + ", type=" + resp.getType(), Toast.LENGTH_SHORT).show();
			SendAuth.Resp authResp = (SendAuth.Resp)resp;
			final String code = authResp.code;
			LogUtil.d(TAG ,"onResp code=" +code);
//			Toast.makeText(this, "code=" +code, Toast.LENGTH_SHORT).show();
			// 获取微信用户accessToken
			ThirdWxLoginManager.Companion.getInstance().loginUseWechat(code);
		}
       finish();
	}

}