package net.sgztech.timeboat.managerUtlis;

import com.alibaba.fastjson.JSON;
import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.SharedPreferencesUtil;
import com.imlaidian.utilslibrary.utilsManager.ThreadPoolManager;

import net.sgztech.timeboat.bleCommand.BallDataModel;
import net.sgztech.timeboat.bleCommand.HikingDataModel;
import net.sgztech.timeboat.bleCommand.MountaineeringDataModel;
import net.sgztech.timeboat.bleCommand.RideDataModel;
import net.sgztech.timeboat.bleCommand.RunDataModel;
import net.sgztech.timeboat.bleCommand.RunningMachineDataModel;
import net.sgztech.timeboat.config.Constants;
import net.sgztech.timeboat.netty.CommandModel;
import net.sgztech.timeboat.netty.CommandTypeUtil;
import net.sgztech.timeboat.netty.CommandUtil;
import net.sgztech.timeboat.netty.MachineNettyClient;
import net.sgztech.timeboat.provide.dataModel.BleDeviceInfoModel;
import net.sgztech.timeboat.provide.dataModel.UserLoginModel;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by Jun on 2022/7/25 8:51 下午.
 */

public final class TCPReportDataManager {
    private static final String TAG = "TCPReportDataManager";
    private static TCPReportDataManager mInstance = null;

    public static TCPReportDataManager getInstance() {
        if (null == mInstance) {
            synchronized (TCPReportDataManager.class) {
                if (null == mInstance) {
                    mInstance = new TCPReportDataManager();
                }
            }
        }

        return mInstance;
    }

    private TCPReportDataManager() {
        sendLKTask();
    }

    private void sendLKTask() {
        long period =  SettingInfoManager.Companion.getInstance().getHeartBeatInterval();
        ThreadPoolManager.getInstance().fixedRate(this::sendLKCommand, 1000, period);
    }

    public void init() {
        LogUtil.d(TAG, "init");
    }

    private String getIMEI() {
        String imei = null;
        BleDeviceInfoModel model = SettingInfoManager.Companion.getInstance().getBleInfo();
        if (null != model) {
            imei = model.getImei();
        }

        LogUtil.d(TAG, "get imei = " + imei);

        return imei;
    }

    public String getUserUUID() {
        String userUUID = null;

        String userInfo = SharedPreferencesUtil.getInstance().getString(Constants.USER_INFO);
        if (StringUtils.isNotEmpty(userInfo)) {
            UserLoginModel userModel = JSON.parseObject(userInfo, UserLoginModel.class);
            if (null != userModel) {
                userUUID = userModel.getUserUUID();
            }
        }

        LogUtil.d(TAG, "get user uuid = " + userUUID);

        return userUUID;
    }
    
    public void sendLKCommand() {
        sendCommand(CommandTypeUtil.buildLKCommand(getIMEI(), getUserUUID()));
    }

    public void sendCoordinateCommand(long deviceTimestampSecond, String lng, String lat) {
        sendCommand(CommandTypeUtil.buildCoordinateCommand(getIMEI(), getUserUUID(), deviceTimestampSecond, lng, lat));
    }

    public void sendStepCommand(long deviceTimestampSecond, int stepCount, int distance, int cal) {
        sendCommand(CommandTypeUtil.buildStepCommand(getIMEI(), getUserUUID(), deviceTimestampSecond, stepCount, distance, cal));
    }

    public void sendHeartRateCommand(long deviceTimestampSecond, int heartRate) {
        sendCommand(CommandTypeUtil.buildHeartRateCommand(getIMEI(), getUserUUID(), deviceTimestampSecond, heartRate));
    }

    public void sendTempCommand(long deviceTimestampSecond, String temp) {
        sendCommand(CommandTypeUtil.buildTempCommand(getIMEI(), getUserUUID(), deviceTimestampSecond, temp));
    }

    public void sendTotalStepCommand(long deviceTimestampSecond, Integer stepCount, Integer distance, Integer cal) {
        sendCommand(CommandTypeUtil.buildTotalStepCommand(getIMEI(), getUserUUID(), deviceTimestampSecond, stepCount, distance, cal));
    }

    public void sendOxygenCommand(long deviceTimestampSecond, int bloodOxygen) {
        sendCommand(CommandTypeUtil.buildOxygenCommand(getIMEI(), getUserUUID(), deviceTimestampSecond, bloodOxygen));
    }

    public void sendSleepCommand(long beginTimestampSecond, int hour, int minute, int state) {
        sendCommand(CommandTypeUtil.buildSleepCommand(getIMEI(), getUserUUID(), beginTimestampSecond, hour, minute, state));
    }

    public void sendRunCommand(long beginTimestampSecond, RunDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildRunCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendHikingCommand(long beginTimestampSecond, HikingDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildHikingCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendRideCommand(long beginTimestampSecond, RideDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildRideCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendMountaineeringCommand(long beginTimestampSecond, MountaineeringDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildMountaineeringCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendRunningMachineCommand(long beginTimestampSecond, RunningMachineDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildRunningMachineCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendBadmintonCommand(long beginTimestampSecond, BallDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildBadmintonCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendBasketballCommand(long beginTimestampSecond, BallDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildBasketballCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    public void sendFootballCommand(long beginTimestampSecond, BallDataModel model) {
        if (null != model) {
            sendCommand(CommandTypeUtil.buildFootballCommand(getIMEI(), getUserUUID(), beginTimestampSecond, model));
        }
    }

    private void sendCommand(CommandModel commandModel) {
        if (null != commandModel) {
            String cmd = CommandUtil.buildCommand(commandModel);
            if (StringUtils.isNotEmpty(cmd)) {
                LogUtil.d(TAG, "send command = " + cmd);

                MachineNettyClient.Companion.getInstance().sendBleCommand(cmd);
            }
        }else{
            LogUtil.d(TAG, "send command null " );
        }
    }
}
