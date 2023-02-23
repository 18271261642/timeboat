package net.sgztech.timeboat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blala.blalable.Utils;


import com.bonlala.widget.view.SwitchButton;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.bean.AlarmBean;
import net.sgztech.timeboat.bean.DbManager;
import net.sgztech.timeboat.listeners.OnCommItemClickListener;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/9/14
 */
public class AlarmAdapterJava extends RecyclerView.Adapter<AlarmAdapterJava.AlarmViewHolder> {

  private Context context;
  private List<AlarmBean> list;

  private OnCommItemClickListener onCommBackDataListener;

    public void setOnCommBackDataListener(OnCommItemClickListener onCommBackDataListener) {
        this.onCommBackDataListener = onCommBackDataListener;
    }

    public AlarmAdapterJava(Context context, List<AlarmBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm_layout,parent,false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmBean alarmBean = list.get(position);
            holder.timeTv.setText(String.format("%02d",alarmBean.getHour())+":"+String.format("%02d",alarmBean.getMinute()));
        holder.switchButton.setChecked(alarmBean.isOpen());
        holder.weekTv.setText(getRepeat((byte) alarmBean.getRepeat()));

        holder.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton button, boolean checked) {
                    if(button.isPressed()){
                        return;
                    }
                    alarmBean.setOpen(checked);
                    DbManager.getInstance().updateAlarm(alarmBean);
                }
            });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getLayoutPosition();
                if(onCommBackDataListener != null){
                    onCommBackDataListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class AlarmViewHolder extends RecyclerView.ViewHolder{

        private TextView timeTv;
        private TextView weekTv;
        private SwitchButton switchButton;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.itemAlarmTimeTv);
            weekTv = itemView.findViewById(R.id.itemAlarmWeekTv);
            switchButton = itemView.findViewById(R.id.itemAlarmSwitch);
        }

    }

    StringBuilder stringBuilder = new StringBuilder();
    private String getRepeat(byte repeat){
        String repeatStr = "";
        Log.e("TAG","----repeat="+repeat);
       stringBuilder.delete(0,stringBuilder.length());
        //转bit
        String bitStr = Utils.byteToBit(repeat);

        byte[] repeatArray = Utils.byteToBitOfArray(repeat);
        Log.e("AD","-bitStr="+bitStr+" "+ Arrays.toString(Utils.byteToBitOfArray(repeat)));

        //[0, 0, 0, 1, 0, 0, 0, 1] 周四，周日
        //每天
        if(repeat == 127){
            return context.getResources().getString(R.string.every_day);
        }
        //周末
        if((repeat & 0xff) == 65){
            return context.getResources().getString(R.string.wenkend_day);
        }
        //工作日
        if((repeat & 0xff) == 62){
            return context.getResources().getString(R.string.work_day);
        }
        if(repeat == 0){
            return context.getResources().getString(R.string.once);
        }

        if(repeatArray[7] == 1){    //周日
            stringBuilder.append(context.getResources().getString(R.string.sun));
        }
        if(repeatArray[6] == 1){    //周一
            stringBuilder.append(context.getResources().getString(R.string.mon));
        }
        if(repeatArray[5] == 1){    //周二
            stringBuilder.append(context.getResources().getString(R.string.tue));
        }
        if(repeatArray[4] == 1){    //周三
            stringBuilder.append(context.getResources().getString(R.string.wed));
        }
        if(repeatArray[3] == 1){    //周四
            stringBuilder.append(context.getResources().getString(R.string.thu));
        }
        if(repeatArray[2] == 1){    //周五
            stringBuilder.append(context.getResources().getString(R.string.fri));
        }
        if(repeatArray[1] == 1){    //周六
            stringBuilder.append(context.getResources().getString(R.string.sat));
        }

        return stringBuilder.toString();

    }
}
