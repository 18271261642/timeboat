package net.sgztech.timeboat.ui.popwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.ui.interfaces.InfoMessageBox;


public class SelectMessageBox extends PopupWindow implements OnClickListener {

	private View           mMenuView;
	private ViewFlipper    viewfipper;	
	private TextView       tv_boxtitle;
	private Button         bt_confirm;
	private Button         bt_cancel;
    private InfoMessageBox onMessageBoxListener;
    
	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	public SelectMessageBox(Activity context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView  = inflater.inflate(R.layout.pw_chooice_box, null);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				                                    LayoutParams.WRAP_CONTENT));

		tv_boxtitle = (TextView) mMenuView.findViewById(R.id.tv_boxtitle);	
		bt_confirm  = (Button) mMenuView.findViewById(R.id.bt_confirm);
		bt_cancel   = (Button) mMenuView.findViewById(R.id.bt_cancel);
		tv_boxtitle.setOnClickListener(this);
		bt_confirm.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
		
		viewfipper.addView(mMenuView);
		viewfipper.setFlipInterval(6000000);
		this.setContentView(viewfipper);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0x70000000);
		this.setBackgroundDrawable(dw);
		this.update();
	}
	
	
	public TextView getTv_boxtitle() {
		return tv_boxtitle;
	}


	public void setTv_boxtitle(String messageTitle) {
		this.tv_boxtitle.setText(messageTitle);
	}


	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		viewfipper.startFlipping();
	}

	public void setMessageBoxListener(InfoMessageBox onMessageBoxListener) {
		this.onMessageBoxListener = onMessageBoxListener;
	}

	@Override
	public void onClick(View v) {
		if (v == bt_confirm) {
			if (onMessageBoxListener != null) {
				onMessageBoxListener.onClick();
			}
			dismiss();
		} else if (v == bt_cancel) {
			dismiss();
		} 
	}
}
