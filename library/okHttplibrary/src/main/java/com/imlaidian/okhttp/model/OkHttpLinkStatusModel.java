package com.imlaidian.okhttp.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by zbo on 17/2/15.
 */

public class OkHttpLinkStatusModel implements Serializable  {
    private static final long serialVersionUID = 4975576882760500754L;
    private int linkStatus;
    private String linkDate;
    private int id;

    public static final int NET_DISCONNECT = 0;
    public static final int NETTY_CLOSED = 1;
    public static final int NET_CONNECTING = 2;
    public static final int NETTY_FETCHING = 3;
    public static final int NETTY_CLOSING_OR_START = 4;
    public static final int NETTY_DNS_ERROR_CLOSED = 5;
    public static final int DNS_REQUEST_SUCCESS = 6;
    public static final int DNS_REQUEST_FAILED = 7;

    /**
     *   错误码
     *   "网络断开" , "netty断开" ,"联网正常","发送命令中" ,"启动或关闭中" ."dns解析异常" ， "dns解析成功" "dns解析失败"
     */
    public static int CODE_NET_DISCONNECT = 10001;
    public static int CODE_NETTY_CLOSED =  10002;
    public static int CODE_NET_CONNECTING =  10003;
    public static int CODE_NETTY_FETCHING =  10004;
    public static int CODE_NETTY_CLOSING_OR_START =  10005;
    public static int CODE_NETTY_DNS_ERROR_CLOSE = 10006;
    public static int CODE_DNS_REQUEST_SUCCESS =  10007;
    public static int CODE_DNS_REQUEST_FAILED =  10008 ;

    private String reserveOne;
    private String reserveTwo;
    private String reserveThree;
    private String reserveFour;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLinkDate() {
        return linkDate;
    }

    public void setLinkDate(String linkDate) {
        this.linkDate = linkDate;
    }

    public int getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(int linkStatus) {
        this.linkStatus = linkStatus;
    }

    public String getReserveFour() {
        return reserveFour;
    }

    public void setReserveFour(String reserveFour) {
        this.reserveFour = reserveFour;
    }

    public String getReserveOne() {
        return reserveOne;
    }

    public void setReserveOne(String reserveOne) {
        this.reserveOne = reserveOne;
    }

    public String getReserveThree() {
        return reserveThree;
    }

    public void setReserveThree(String reserveThree) {
        this.reserveThree = reserveThree;
    }

    public String getReserveTwo() {
        return reserveTwo;
    }

    public void setReserveTwo(String reserveTwo) {
        this.reserveTwo = reserveTwo;
    }


    private static final Map<Integer, String> CODE_MAP = new HashMap<Integer, String>() {
        private static final long serialVersionUID = -7692258635952420533L;
        {

            put(CODE_NET_DISCONNECT , "network disconnect");
            put(CODE_NETTY_CLOSED , "netty disconnect");
            put(CODE_NET_CONNECTING , "network connect");
            put(CODE_NETTY_FETCHING , "sending command");
            put(CODE_NETTY_CLOSING_OR_START , "netty close");
            put(CODE_NETTY_DNS_ERROR_CLOSE , "dns parsing unusual");
            put(CODE_DNS_REQUEST_SUCCESS , "dns parsing success");
            put(CODE_DNS_REQUEST_FAILED , "dns parsing failed");

        }
    };


    public static String getCodeString(int code) {
        if (CODE_MAP.containsKey(code)) {
            return CODE_MAP.get(code);
        }

        return null;
    }

    private void getCodeMapValue(Map<Integer, String> map, int code) {
        if (CODE_MAP.containsKey(code)) {
            map.put(code, CODE_MAP.get(code));
        }
    }

    public Map<Integer, String> getCodeMap() {
        Map<Integer ,String> codeMap = new Hashtable<>();
        int status = getLinkStatus() ;
        switch (status){
            case NET_DISCONNECT :
                codeMap.put(CODE_NET_DISCONNECT , "network disconnect");
                break;
            case NETTY_CLOSED :
                codeMap.put(CODE_NETTY_CLOSED , "netty disconnect");
                break;
            case NET_CONNECTING:
                codeMap.put(CODE_NET_CONNECTING , "network connect");
                break;
            case NETTY_FETCHING:
                codeMap.put(CODE_NETTY_FETCHING , "sending command");
                break;
            case NETTY_CLOSING_OR_START:
                codeMap.put(CODE_NETTY_CLOSING_OR_START , "netty close");
                break;
            case NETTY_DNS_ERROR_CLOSED:
                codeMap.put(CODE_NETTY_DNS_ERROR_CLOSE , "dns parsing unusual");
                break;
            case DNS_REQUEST_SUCCESS:
                codeMap.put(CODE_DNS_REQUEST_SUCCESS , "dns parsing success");
                break;
            case DNS_REQUEST_FAILED:
                codeMap.put(CODE_DNS_REQUEST_FAILED , "dns parsing failed");
                break;
        }

        return  codeMap;
    }
}
