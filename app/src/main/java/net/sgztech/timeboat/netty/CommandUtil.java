package net.sgztech.timeboat.netty;


import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUtil {
    // 命令格式：[厂商*设备ID*内容长度*内容]
    private static final String PREFIX = "[";
    private static final String SUFFIX = "]";
    private static final String COMMA = ",";
    private static final String SEP = "*";

    public static String buildCommand(String header, String imei, String cmdType, List<String> data) {
        if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(cmdType)) {
            String dataStr = "";
            if (null != data && data.size() > 0) {
                dataStr = StringUtils.join(data, COMMA);
            }

            if (null != dataStr && dataStr.length() > 0) {
                dataStr = COMMA + dataStr;
            } else {
                dataStr = "";
            }

            int len = cmdType.length() + dataStr.length();
            String strLen = StringUtils.leftPad(Integer.toHexString(len), 4, "0");

            return PREFIX
                    + header + SEP + imei + SEP
                    + strLen + SEP + cmdType
                    + dataStr
                    + SUFFIX;
        }

        return null;
    }

    public static String buildCommand(CommandModel commandModel) {
        if (null != commandModel) {
            return buildCommand(commandModel.getHeader(),
                    commandModel.getImei(),
                    commandModel.getCmdType(),
                    commandModel.getDataList());
        }

        return null;
    }

    public static CommandModel buildCommandModel(String header, String imei, String cmdType, List<String> data) {
        if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(cmdType)) {
            return CommandModel.build()
                    .setHeader(header)
                    .setImei(imei)
                    .setCmdType(cmdType)
                    .setDataList(data);
        }

        return null;
    }

    public static String responseCommand(String header, String imei, String cmdType) {
        if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(cmdType)) {
            int len = cmdType.length();
            String strLen = StringUtils.leftPad(Integer.toHexString(len), 4, "0");

            return PREFIX
                    + header + SEP + imei + SEP
                    + strLen + SEP + cmdType
                    + SUFFIX;
        }

        return null;
    }

    public static String responseCommand(CommandModel commandModel) {
        if (null != commandModel) {
            return responseCommand(commandModel.getHeader(),
                    commandModel.getImei(),
                    commandModel.getCmdType());
        }

        return null;
    }

    public static CommandModel responseCommandModel(String header, String imei, String cmdType) {
        if (StringUtils.isNotEmpty(header) && StringUtils.isNotEmpty(imei) && StringUtils.isNotEmpty(cmdType)) {
            return CommandModel.build()
                    .setHeader(header)
                    .setImei(imei)
                    .setCmdType(cmdType);
        }

        return null;
    }

    public static CommandModel responseCommandModel(CommandModel commandModel) {
        if (null != commandModel) {
            return responseCommandModel(commandModel.getHeader(), commandModel.getImei(), commandModel.getCmdType());
        }

        return null;
    }

    public static byte[] responseCommandBytes(CommandModel commandModel) {
        if (null != commandModel) {
            String res = responseCommand(commandModel);
            if (StringUtils.isNotEmpty(res)) {
                return res.getBytes();
            }
        }

        return null;
    }

    public static List<CommandModel> parseCommand(byte[] data) {
        if (null != data) {
            String cmd = new String(data);

            return parseCommand(cmd);
        }

        return null;
    }

    public static String dataListToString(List<String> dataList) {
        if (null != dataList) {
            return StringUtils.join(dataList, CommandUtil.COMMA);
        }

        return null;
    }

    public static CommandModel buildCommandModel(String imei, String header, String cmdType, String cmdData) {
        CommandModel commandModel = CommandModel.build()
                .setImei(imei)
                .setHeader(header)
                .setCmdType(cmdType);

        if (StringUtils.isNotEmpty(cmdData)) {
            commandModel.setDataList(Arrays.asList(cmdData.split(COMMA)));
        }

        return commandModel;
    }

    private static int strToInt(String str, int radix) {
        int param = 0;

        if (null != str) {
            try {
                param = Integer.parseInt(str, radix);
            } catch (NumberFormatException var3) {
                var3.printStackTrace();
            }
        }

        return param;
    }

    public static List<CommandModel> parseCommand(String readCommand) {
        List<CommandModel> cmdList = new ArrayList<>();

        int prefixIndex = -1;
        int suffixIndex = -1;
        int beginIndex = -1;
        int endIndex = -1;
        String cmd = null;
        CommandModel item = null;

        if (StringUtils.isNotEmpty(readCommand)) {
            String command = readCommand;
            while (true) {
                prefixIndex = command.indexOf(PREFIX);
                if (prefixIndex < 0) {
                    Log.d("CommandUtils","prefixIndex ="+ prefixIndex + ", command=" +command);
                    break;
                }

                suffixIndex = command.indexOf(SUFFIX);
                if (suffixIndex < 0) {
                    Log.d("CommandUtils","suffixIndex ="+ suffixIndex + ", command=" +command);
                    break;
                }

                beginIndex = prefixIndex;
                endIndex = suffixIndex + 1;
                if ((endIndex - beginIndex) <= 0) {
                    Log.d("CommandUtils","beginIndex ="+ beginIndex + ",endIndex=" +endIndex+ ", command=" +command);
                    break;
                }

                cmd = command.substring(prefixIndex, endIndex);
                command = command.substring(endIndex);

                String trimCmd = cmd.replace(PREFIX, "").replace(SUFFIX, "");
                String[] cmds = trimCmd.split(COMMA);

                if (cmds.length > 0) {
                    for (int i = 0; i < cmds.length; i++) {
                        String parseStr = cmds[i];
                        if (0 == i) {
                            // command
                            String[] items = parseStr.split("\\" + SEP);
                            if (items.length >= 4) {
                                String checksum = null;
                                String header = items[0];
                                String imei = items[1];
                                int len = strToInt(items[2], 16);
                                String cmdType = items[3];
                                CommandHeaderEnum headerEnum = CommandHeaderEnum.getTypeEnum(header);

                                if (null != headerEnum) {
                                    if (headerEnum.isPrefix()) {
                                        checksum = "";
                                    } else if (headerEnum.isSuffix()) {
                                        header = headerEnum.getName();
                                        checksum = header.substring(0, header.length() - headerEnum.getName().length());
                                    }
                                }

                                item = CommandModel.build()
                                        .setHeader(header)
                                        .setImei(imei)
                                        .setLen(len)
                                        .setCmdType(cmdType)
                                        .setDataList(new ArrayList<>())
                                        .setSourceCommand(cmd)
                                        .setChecksum(checksum);
                                cmdList.add(item);
                            } else {
                                break;
                            }
                        } else {
                            if (null != item && null != item.getDataList()) {
                                item.getDataList().add(parseStr);
                            }
                        }
                    }
                }
            }
        }

        return cmdList;
    }
}
