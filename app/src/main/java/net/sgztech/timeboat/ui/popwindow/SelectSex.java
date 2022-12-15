package net.sgztech.timeboat.ui.popwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.device.ui.widget.wheelview.OnWheelChangedListener;
import com.device.ui.widget.wheelview.OnWheelScrollListener;
import com.device.ui.widget.wheelview.WheelView;
import com.device.ui.widget.wheelview.adapter.AbstractWheelTextAdapter;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.ui.utils.UIUtils;

import java.util.ArrayList;


public class SelectSex extends PopupWindow implements OnClickListener {

	@SuppressWarnings("unused")
	private Activity      mContext;
	private View          mMenuView;
	private ViewFlipper   viewfipper;
	private Button        btn_submit, btn_cancel;
	private WheelView mViewSex;

	private ArrayList<String>  arrSexs = new ArrayList<String>();
	private String         strManSex   = "男";
	private String         strWomenSex = "女";
	
	private OnSexCListener onSexCListener;
	private SexTextAdapter sexAdapter;
	
	private int    maxsize    = 24;
	private int    minsize    = 14;
	
	private int    currSexid  =0;
	private String strCurrSex = "女";
    
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	public SelectSex(Activity context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView  = inflater.inflate(R.layout.pw_chooice_sex, null);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				                                    LayoutParams.WRAP_CONTENT));

		mViewSex   = (WheelView) mMenuView.findViewById(R.id.sex);
		btn_submit = (Button) mMenuView.findViewById(R.id.submit);
		btn_cancel = (Button) mMenuView.findViewById(R.id.cancel);
		btn_submit.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		arrSexs.add(strWomenSex);
		arrSexs.add(strManSex);
		
		sexAdapter = new SexTextAdapter(context, arrSexs, 0, maxsize, minsize);
		mViewSex.setVisibleItems(2);
		mViewSex.setViewAdapter(sexAdapter);
		mViewSex.setCurrentItem(0);
		sexAdapter.setTextColor(Color.rgb(138, 133, 133));
		
		mViewSex.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				String currentText = (String) sexAdapter.getItemText(wheel.getCurrentItem());
				strCurrSex = currentText;
				currSexid  = wheel.getCurrentItem();
			}
		});

		mViewSex.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
				String currentText = (String) sexAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, sexAdapter);
			}
		});
		
		viewfipper.addView(mMenuView);
		viewfipper.setFlipInterval(6000000);
		this.setContentView(viewfipper);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		this.update();

	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		viewfipper.startFlipping();
	}

	public void setSexListener(OnSexCListener onSexCListener) {
		this.onSexCListener = onSexCListener;
	}
	

	
	public void onClick(View v) {
		if (v == btn_submit) {
			UIUtils.backgroundAlpha(mContext, 1f);
			if (onSexCListener != null) {
				onSexCListener.onClick(currSexid, strCurrSex);
			}
		} else if (v == btn_cancel) {
			UIUtils.backgroundAlpha(mContext, 1f);
		} else {
			dismiss();
		}
		dismiss();
	}

	/**
	 * 设置字体大小
	 * 
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText, SexTextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(24);
			} else {
				textvew.setTextSize(14);
			}
		}
	}
	
	
	/**
	 * 回调接口
	 *
	 */
	public interface OnSexCListener {
		void onClick(int sexid, String sex);
	}
	
	private class SexTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected SexTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
			super(context, R.layout.item_select_txt, NO_RESOURCE, currentItem, maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.temp_value);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index) + "";
		}
	}
}
