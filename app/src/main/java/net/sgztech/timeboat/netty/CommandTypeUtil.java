package net.sgztech.timeboat.netty;

import net.sgztech.timeboat.bleCommand.BallDataModel;
import net.sgztech.timeboat.bleCommand.HikingDataModel;
import net.sgztech.timeboat.bleCommand.MountaineeringDataModel;
import net.sgztech.timeboat.bleCommand.RideDataModel;
import net.sgztech.timeboat.bleCommand.RunDataModel;
import net.sgztech.timeboat.bleCommand.RunningMachineDataModel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jun on 2022/7/25 8:15 下午.
 */

public final class CommandTypeUtil {
   public static final String LK = "LK";
   public static final String COORDINATE = "COORDINATE";
   public static final String STEP = "STEP";
   public static final String HEART_RATE = "HEARTRATE";
   public static final String TEMP = "TEMP";
   public static final String TOTAL_STEP = "TOTALSTEP";
   public static final String OXYGEN = "OXYGEN";
   public static final String SLEEP = "SLEEP";
   public static final String RUN = "RUN";
   public static final String HIKING = "HIKING";
   public static final String RIDE = "RIDE";
   public static final String MOUNTAINEERING = "MOUNTAINEERING";
   public static final String RUNNINGMACHINE = "RUNNINGMACHINE";
   public static final String BADMINTON = "BADMINTON";
   public static final String BASKETBALL = "BASKETBALL";
   public static final String FOOTBALL = "FOOTBALL";

   private static final String HEADER = CommandHeaderEnum.H_SGZ_A.getName();

   private CommandTypeUtil() {

   }

   public static CommandModel buildLKCommand(String imei, String userUUID) {
      if (StringUtils.isNotEmpty(imei)) {
         return CommandUtil.buildCommandModel(HEADER, imei, LK, Collections.singletonList(userUUID));
      }

      return null;
   }

   public static CommandModel buildCoordinateCommand(String imei, String userUUID, long deviceTimestampSecond, String lng, String lat) {
      if (StringUtils.isNotEmpty(imei)
              && StringUtils.isNotEmpty(userUUID)
              && StringUtils.isNotEmpty(lng)
              && StringUtils.isNotEmpty(lat)) {
         List<String> dataList = Arrays.asList(userUUID, lng, lat);

         return CommandUtil.buildCommandModel(HEADER, imei, COORDINATE, dataList);
      }

      return null;
   }

   public static CommandModel buildStepCommand(String imei, String userUUID, long deviceTimestampSecond, int stepCount, int distance, int cal) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID)) {
         List<String> dataList = Arrays.asList(
                 userUUID, "" + deviceTimestampSecond, "" + stepCount, "" + distance, "" + cal
         );

         return CommandUtil.buildCommandModel(HEADER, imei, STEP, dataList);
      }

      return null;
   }

   public static CommandModel buildHeartRateCommand(String imei, String userUUID, long deviceTimestampSecond, int heartRate) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID)) {
         List<String> dataList = Arrays.asList(
                 userUUID, "" + deviceTimestampSecond, "" + heartRate
         );

         return CommandUtil.buildCommandModel(HEADER, imei, HEART_RATE, dataList);
      }

      return null;
   }

   public static CommandModel buildTempCommand(String imei, String userUUID, long deviceTimestampSecond, String temp) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID)) {
         List<String> dataList = Arrays.asList(
                 userUUID, "" + deviceTimestampSecond, "" + temp
         );

         return CommandUtil.buildCommandModel(HEADER, imei, TEMP, dataList);
      }

      return null;
   }

   public static CommandModel buildTotalStepCommand(String imei, String userUUID, long deviceTimestampSecond, int stepCount, int distance, int cal) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID)) {
         List<String> dataList = Arrays.asList(
                 userUUID,
                 "" + deviceTimestampSecond,
                 "" + stepCount,
                 "" + distance,
                 "" + cal
         );

         return CommandUtil.buildCommandModel(HEADER, imei, TOTAL_STEP, dataList);
      }

      return null;
   }

   public static CommandModel buildOxygenCommand(String imei, String userUUID, long deviceTimestampSecond, int bloodOxygen) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID)) {
         List<String> dataList = Arrays.asList(
                 userUUID,
                 "" + deviceTimestampSecond,
                 "" + bloodOxygen
         );

         return CommandUtil.buildCommandModel(HEADER, imei, OXYGEN, dataList);
      }

      return null;
   }

   public static CommandModel buildSleepCommand(String imei, String userUUID, long beginTimestampSecond, int hour, int minute, int state) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID)) {
         List<String> dataList = Arrays.asList(
                 userUUID,
                 "" + beginTimestampSecond,
                 "" + hour,
                 "" + minute,
                 "" + state
         );

         return CommandUtil.buildCommandModel(HEADER, imei, SLEEP, dataList);
      }

      return null;
   }

   public static CommandModel buildRunCommand(String imei, String userUUID, long beginTimestampSecond, RunDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 模式
         dataList.add("" + (model.getModel() & 0xff));
         // 总计步
         dataList.add("" + model.getStepCount());
         // 实际里程(米)
         dataList.add("" + model.getActualDistance());
         // 实际用时(秒)
         dataList.add("" + model.getActualUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 配速长度
         // 配速...(秒/公里)
         if (null != model.getPaceList() && model.getPaceList().size() > 0) {
            dataList.add("" + model.getPaceList().size());
            for (Short pace : model.getPaceList()) {
               dataList.add("" + pace);
            }
         } else {
            dataList.add("0");
         }
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }
         // 步频长度
         // 步频...
         if (null != model.getStepFrequencyList() && model.getStepFrequencyList().size() > 0) {
            dataList.add("" + model.getStepFrequencyList().size());
            for (Short step : model.getStepFrequencyList()) {
               dataList.add("" + step);
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, RUN, dataList);
      }

      return null;
   }

   public static CommandModel buildHikingCommand(String imei, String userUUID, long beginTimestampSecond, HikingDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 总计步
         dataList.add("" + model.getStepCount());
         // 实际里程(米)
         dataList.add("" + model.getDistance());
         // 实际用时(秒)
         dataList.add("" + model.getUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 配速长度
         // 配速...(秒/公里)
         if (null != model.getPaceList() && model.getPaceList().size() > 0) {
            dataList.add("" + model.getPaceList().size());
            for (Short pace : model.getPaceList()) {
               dataList.add("" + pace);
            }
         } else {
            dataList.add("0");
         }
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }
         // 步频长度
         // 步频...
         if (null != model.getStepFrequencyList() && model.getStepFrequencyList().size() > 0) {
            dataList.add("" + model.getStepFrequencyList().size());
            for (Short step : model.getStepFrequencyList()) {
               dataList.add("" + step);
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, HIKING, dataList);
      }

      return null;
   }

   public static CommandModel buildRideCommand(String imei, String userUUID, long beginTimestampSecond, RideDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 模式
         dataList.add("" + model.getModel());
         // 实际里程(米)
         dataList.add("" + model.getActualDistance());
         // 实际用时(秒)
         dataList.add("" + model.getActualUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 配速长度
         // 配速...(秒/公里)
         if (null != model.getPaceList() && model.getPaceList().size() > 0) {
            dataList.add("" + model.getPaceList().size());
            for (Short pace : model.getPaceList()) {
               dataList.add("" + pace);
            }
         } else {
            dataList.add("0");
         }
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, RIDE, dataList);
      }

      return null;
   }

   public static CommandModel buildMountaineeringCommand(String imei, String userUUID, long beginTimestampSecond, MountaineeringDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 总计步
         dataList.add("" + model.getStepCount());
         // 实际里程(米)
         dataList.add("" + model.getDistance());
         // 实际用时(秒)
         dataList.add("" + model.getUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, MOUNTAINEERING, dataList);
      }

      return null;
   }

   public static CommandModel buildRunningMachineCommand(String imei, String userUUID, long beginTimestampSecond, RunningMachineDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 总计步
         dataList.add("" + model.getStepCount());
         // 实际用时(秒)
         dataList.add("" + model.getUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }
         // 步频长度
         // 步频...
         if (null != model.getStepFrequencyList() && model.getStepFrequencyList().size() > 0) {
            dataList.add("" + model.getStepFrequencyList().size());
            for (Short step : model.getStepFrequencyList()) {
               dataList.add("" + step);
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, RUNNINGMACHINE, dataList);
      }

      return null;
   }

   public static CommandModel buildBadmintonCommand(String imei, String userUUID, long beginTimestampSecond, BallDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 实际用时(秒)
         dataList.add("" + model.getUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, BADMINTON, dataList);
      }

      return null;
   }

   public static CommandModel buildBasketballCommand(String imei, String userUUID, long beginTimestampSecond, BallDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 实际用时(秒)
         dataList.add("" + model.getUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, BASKETBALL, dataList);
      }

      return null;
   }

   public static CommandModel buildFootballCommand(String imei, String userUUID, long beginTimestampSecond, BallDataModel model) {
      if (StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(userUUID) && null != model) {
         List<String> dataList = new ArrayList<>();
         dataList.add(userUUID);
         dataList.add("" + beginTimestampSecond);
         // 实际用时(秒)
         dataList.add("" + model.getUseTime());
         // 卡路里(卡)
         dataList.add("" + model.getCalorie());
         // 心率长度
         // 心率...
         if (null != model.getHeartRateList() && model.getHeartRateList().length > 0) {
            dataList.add("" + model.getHeartRateList().length);
            for (Byte heartRate : model.getHeartRateList()) {
               dataList.add("" + (heartRate & 0xff));
            }
         } else {
            dataList.add("0");
         }

         return CommandUtil.buildCommandModel(HEADER, imei, FOOTBALL, dataList);
      }

      return null;
   }
}
