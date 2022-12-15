package net.sgztech.timeboat.ui.popwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.device.ui.widget.wheelview.OnWheelChangedListener;
import com.device.ui.widget.wheelview.WheelView;
import com.device.ui.widget.wheelview.adapter.NumericWheelAdapter;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.ui.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectWeight extends PopupWindow implements View.OnClickListener {

    @SuppressWarnings("unused")
    private Activity mContext;
    private View mMenuView;
    private ViewFlipper viewfipper;
    private Button btn_submit, btn_cancel;
    private WheelView mViewWeight;

    private List<String> arrWeight = new ArrayList<String>();

    private OnWeightListener onWeightListener;


    private int maxWeight = 200;

    private int currWeightId = 60;
    private String strCurrWeight = "60";

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    public SelectWeight(Activity context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pw_chooice_weight, null);
        viewfipper = new ViewFlipper(context);
        viewfipper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mViewWeight = (WheelView) mMenuView.findViewById(R.id.weight);
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

    private void initData() {
        if (arrWeight != null & arrWeight.size() > 0) {
            arrWeight.clear();
        }

        for (int i = 0; i <= maxWeight; i++) {
            arrWeight.add(i + "");
        }
    }

    private void initWheelView() {
        NumericWheelAdapter numericAdapter = new NumericWheelAdapter(mContext, 0, maxWeight);
        numericAdapter.setTextSize(18);
        numericAdapter.setLabel("公斤");

        mViewWeight.setViewAdapter(numericAdapter);
        mViewWeight.setCurrentItem(currWeightId);
        mViewWeight.setCyclic(true);// 可循环滚动
        mViewWeight.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                strCurrWeight = "" + newValue;
                currWeightId = wheel.getCurrentItem();

            }
        });

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        viewfipper.startFlipping();
    }

    public void setWeightListener(OnWeightListener onWeightListener ) {
        this.onWeightListener = onWeightListener;
    }

    /**
     * 初始化高度
     *
     * @param weightId
     */
    public void setWeight(int weightId) {

        if (weightId < maxWeight) {

            this.strCurrWeight = weightId + "";

            this.currWeightId = weightId;

            mViewWeight.setCurrentItem(weightId);
        } else {
            Toast.makeText(mContext, "设置高度错误", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 回调接口
     *
     */
    public interface OnWeightListener {
        void onClick(int weightId, String weight);
    }

    public void onClick(View v) {
        if (v == btn_submit) {
            UIUtils.backgroundAlpha(mContext, 1f);
            if (onWeightListener != null) {
                onWeightListener.onClick(currWeightId, strCurrWeight);
            }
        } else if (v == btn_cancel) {
            UIUtils.backgroundAlpha(mContext, 1f);
        } else {
            dismiss();
        }
        dismiss();
    }
}