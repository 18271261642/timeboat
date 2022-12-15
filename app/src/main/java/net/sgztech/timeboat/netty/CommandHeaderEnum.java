package net.sgztech.timeboat.netty;


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum CommandHeaderEnum {
    H_SGZ_A("SGZA", 500, true, false, "时光舟-A协议"),
    ;

    private static final Map<String, CommandHeaderEnum> NAME_MAP;

    static {
        NAME_MAP = new HashMap<>();
        NAME_MAP.put(H_SGZ_A.getName(), H_SGZ_A);
    }

    CommandHeaderEnum(String name, Integer code, boolean prefix, boolean suffix, String desc) {
        mName = name;
        mCode = code;
        mPrefix = prefix;
        mSuffix = suffix;
        mDesc = desc;
    }

    private final String mName;
    private final Integer mCode;
    private final boolean mPrefix;
    private final boolean mSuffix;
    private final String mDesc;

    public String getName() {
        return mName;
    }

    public String getUpperCaseName() {
        return mName.toUpperCase();
    }

    public boolean isPrefix() {
        return mPrefix;
    }

    public boolean isSuffix() {
        return mSuffix;
    }

    public Integer getCode() {
        return mCode;
    }

    public String getDesc() {
        return mDesc;
    }

    public static boolean isHSGZA(String header) {
        return isHeader(H_SGZ_A, header);
    }

    public static CommandHeaderEnum getTypeEnum(String name) {
        if (StringUtils.isNotEmpty(name)) {
            for (CommandHeaderEnum headerEnum : NAME_MAP.values()) {
                if (isHeader(headerEnum, name)) {
                    return headerEnum;
                }
            }
        }

        return null;
    }

    public static boolean isHeader(CommandHeaderEnum headerEnum, String headerName) {
        if (null != headerEnum && StringUtils.isNotEmpty(headerName)) {
            if (headerEnum.isPrefix()) {
                return headerName.toUpperCase().startsWith(headerEnum.getName().toUpperCase());
            }

            if (headerEnum.isSuffix()) {
                return headerName.toUpperCase().endsWith(headerEnum.getName().toUpperCase());
            }
        }

        return false;
    }

    public static boolean isValid(String name) {
        return null != getTypeEnum(name);
    }

    public static boolean isInvalid(String name) {
        return !isValid(name);
    }

    public static boolean isPrefix(String name) {
        if (StringUtils.isNotEmpty(name)) {
            CommandHeaderEnum headerEnum = getTypeEnum(name);
            if (null != headerEnum) {
                return headerEnum.isPrefix();
            }
        }

        return false;
    }

    public static boolean isSuffix(String name) {
        if (StringUtils.isNotEmpty(name)) {
            CommandHeaderEnum headerEnum = getTypeEnum(name);
            if (null != headerEnum) {
                return headerEnum.isSuffix();
            }
        }

        return false;
    }
}
