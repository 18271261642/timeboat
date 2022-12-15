

package com.imlaidian.ldclog;

public class ConstantCode {

    public static class ClogStatus {
        public static final String CLOG_INIT_STATUS = "clog_init"; //初始化函数
        public static final int CLOG_INIT_SUCCESS_MMAP = -1010; //初始化成功, mmap内存
        public static final int CLOG_INIT_SUCCESS_MEMORY = -1020; //初始化成功, 堆内存
        public static final int CLOG_INIT_FAIL_NOCACHE = -1030; //初始化失败 , 没有缓存
        public static final int CLOG_INIT_FAIL_NOMALLOC = -1040; //初始化失败 , malloc失败
        public static final int CLOG_INIT_FAIL_HEADER = -1050; //初始化头失败
        public static final int CLOG_INIT_FAIL_JNI = -1060; //jni找不到对应C函数

        public static final String CLOG_OPEN_STATUS = "clog_open"; //打开文件函数
        public static final int CLOG_OPEN_SUCCESS = -2010; //打开文件成功
        public static final int CLOG_OPEN_FAIL_IO = -2020; //打开文件IO失败
        public static final int CLOG_OPEN_FAIL_ZLIB = -2030; //打开文件zlib失败
        public static final int CLOG_OPEN_FAIL_MALLOC = -2040; //打开文件malloc失败
        public static final int CLOG_OPEN_FAIL_NOINIT = -2050; //打开文件没有初始化失败
        public static final int CLOG_OPEN_FAIL_HEADER = -2060; //打开文件头失败
        public static final int CLOG_OPEN_FAIL_JNI = -2070; //jni找不到对应C函数

        public static final String CLOG_WRITE_STATUS = "clog_write"; //写入函数
        public static final int CLOG_WRITE_SUCCESS = -4010; //写入日志成功
        public static final int CLOG_WRITE_FAIL_PARAM = -4020; //写入失败, 可变参数错误
        public static final int CLOG_WRITE_FAIL_MAXFILE = -4030; //写入失败,超过文件最大值
        public static final int CLOG_WRITE_FAIL_MALLOC = -4040; //写入失败,malloc失败
        public static final int CLOG_WRITE_FAIL_HEADER = -4050; //写入头失败
        public static final int CLOG_WRITE_FAIL_JNI = -4060; //jni找不到对应C函数

        public static final String CLOG_LOAD_SO = "log_loadso"; //Log装载So;
        public static final int CLOG_LOAD_SO_FAIL = -5020; //加载的SO失败
    }
}
