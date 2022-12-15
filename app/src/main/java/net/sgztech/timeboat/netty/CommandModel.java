package net.sgztech.timeboat.netty;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CommandModel {
    private String header;

    private String imei;

    private int len;

    private String cmdType;

    private List<String> dataList;

    private String sourceCommand;

    private String checksum;

    private long timestamp = System.currentTimeMillis();

    public static CommandModel build() {
        return new CommandModel();
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public static CommandModel parseJsonString(String json) {
        if (StringUtils.isNotEmpty(json)) {
            return JSON.parseObject(json, CommandModel.class);
        }

        return null;
    }

    public CommandModel setHeader(String header) {
        this.header = header;

        return this;
    }

    public CommandModel setImei(String imei) {
        this.imei = imei;

        return this;
    }

    public CommandModel setLen(int len) {
        this.len = len;

        return this;
    }

    public CommandModel setCmdType(String cmdType) {
        this.cmdType = cmdType;

        return this;
    }

    public CommandModel setDataList(List<String> dataList) {
        this.dataList = dataList;

        return this;
    }

    public CommandModel setSourceCommand(String sourceCommand) {
        this.sourceCommand = sourceCommand;

        return this;
    }

    public CommandModel setChecksum(String checksum) {
        this.checksum = checksum;

        return this;
    }

    public boolean isHeaderValid() {
        return StringUtils.isNotEmpty(this.header);
    }

    public boolean isImeiValid() {
        return StringUtils.isNotEmpty(this.imei);
    }

    public boolean isLenValid() {
        return this.len > 0;
    }

    public boolean isCmdTypeValid() {
        return StringUtils.isNotEmpty(this.cmdType);
    }

    public String getHeader() {
        return header;
    }

    public String getImei() {
        return imei;
    }

    public int getLen() {
        return len;
    }

    public String getCmdType() {
        return cmdType;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public String getSourceCommand() {
        return sourceCommand;
    }

    public String getChecksum() {
        return checksum;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
