package net.sgztech.timeboat.ui.popwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.device.ui.widget.wheelview.OnWheelChangedListener;
import com.device.ui.widget.wheelview.OnWheelScrollListener;
import com.device.ui.widget.wheelview.WheelView;
import com.device.ui.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.device.ui.widget.wheelview.adapter.NumericWheelAdapter;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.ui.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SelectHeight extends PopupWindow implements View.OnClickListener {

    @SuppressWarnings("unused")
    private Activity mContext;
    private View mMenuView;
    private ViewFlipper viewfipper;
    private Button btn_submit, btn_cancel;
    private WheelView mViewHeight;

    private List<String> arrHeight = new ArrayList<String>();

    private OnHeightListener onHeightListener;

    private int maxHeight = 260;

    private int currHeightId = 170;
    private String strCurrHeight = "170";

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    public SelectHeight(Activity context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pw_chooice_height, null);
        viewfipper = new ViewFlipper(context);
        viewfipper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mViewHeight = (WheelView) mMenuView.findViewById(R.id.height);
        btn_submit = (Button) mMenuView.findViewById(R.id.submit);
        btn_cancel = (Button) mMenuView.findViewById(R.id.cancel);
        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        initData();
        initWheelView();

        viewfipper.addView(mMenuView);
        viewfipper.setFlipInterval(6000000);
        this.setContentView(viewfipper);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.update();

    }
    private void initData(){
        if(arrHeight!= null&arrHeight.size()>0){
            arrHeight.clear();
        }

        for(int i = 0 ; i <= maxHeight ; i++){
            arrHeight.add(i + "");
        }
    }

    private void initWheelView(){
        NumericWheelAdapter numericAdapter = new NumericWheelAdapter(mContext, 0, maxHeight);
        numericAdapter.setTextSize(18);
        numericAdapter.setLabel("厘米");
        mViewHeight.setViewAdapter(numericAdapter);
        mViewHeight.setCyclic(true);// 可循环滚动
        mViewHeight.setCurrentItem(currHeightId);
        mViewHeight.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                strCurrHeight ="" + newValue ;
                currHeightId = wheel.getCurrentItem();

            }
        });

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        viewfipper.startFlipping();
    }

    public void setHeightListener(OnHeightListener onHeightListener) {
        this.onHeightListener = onHeightListener;
    }

    /**
     * 初始化高度
     *
     * @param heightId
     */
    public void setHeightId(int heightId) {

        if(heightId < maxHeight){

           this.strCurrHeight = heightId +"";

            this.currHeightId = heightId;

            mViewHeight.setCurrentItem(heightId);
        }else{
            Toast.makeText(mContext,"设置高度错误",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 回调接口
     *
     * @author Administrator
     */
    public interface OnHeightListener {
        void onClick(int heightId, String height);
    }

    public void onClick(View v) {
        if (v == btn_submit) {
            UIUtils.backgroundAlpha(mContext, 1f);
            if (onHeightListener != null) {
                onHeightListener.onClick(currHeightId, strCurrHeight);
            }
        } else if (v == btn_cancel) {
            UIUtils.backgroundAlpha(mContext, 1f);
        } else {
            dismiss();
        }
        dismiss();
    }

}