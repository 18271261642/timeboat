package com.imlaidian.utilslibrary.config;

/**
 * Created by zbo on 16/7/22.
 */
public class IntentConstant {
    public static final String  BORROW_CDB_NICK_NAME = "borrow_nick_name" ;
    public static final String  BORROW_CDB_HEAD_URL = "borrow_head_url";
    public static final String  BORROW_CDB_COME_FROM = "borrow_come_from";
    public static final String  BORROW_CDB_CHANNEL_POSITION ="borrow_cdb_channel_position";
    public static final String  BORROW_CDB_TIPS_TYPE = "borrow_cdb_tips_type";
    public static final int     BORROW_CDB_TIPS_TYPE_PREPARE = 0;
    public static final int     BORROW_CDB_TIPS_TYPE_TAKE_AWAY = 1;
    public static final int     BORROW_CDB_TIPS_TYPE_REPEAT = 2;
    public static final String  MACHINE_FAQ_TYPE ="faq_type";
    public static final String  MACHINE_FAQ_TYPE_DATA ="faq_type_data";
    // faq 提示 重启
    public static final int     MACHINE_FAQ_TYPE_POWER_REBOOT = 1;
    public static final int     MACHINE_FAQ_TYPE_ROUTE_REBOOT = 2 ;
    public static final int     MACHINE_FAQ_TYPE_OTHER_REBOOT = 3;
    public static final int     MACHINE_FAQ_TYPE_SERIAL_REBOOT= 4 ;
    //faq 提示 但不重启
    public static final int     MACHINE_FAQ_TYPE_SERIAL_ERROR= 5 ;
    // 仓道拉丝 错误
    public static final int     MACHINE_FAQ_TYPE_CHECK_ERROR  = 6;
    // 主板远程关机
    public static final int     MACHINE_FAQ_TYPE_REMOTE_POWER_OFF  = 7;
    // 主板远程开机
    public static final int     MACHINE_FAQ_TYPE_REMOTE_POWER_ON  = 8;
    // 机器远程关继电器
    public static final int     MACHINE_FAQ_TYPE_REMOTE_RELAY_OFF  = 9;
    // 机器远程关机电器，在开继电器
    public static final int     MACHINE_FAQ_TYPE_REMOTE_RELAY_ON  = 10;

    // 机器app crash 异常
    public static final int     MACHINE_FAQ_TYPE_APP_CRASH = 11 ;

    // 烟雾报警异常 继电器掉电
    public static final int     MACHINE_FAQ_TYPE_SMK_WARMING_RELAY_OFF = 12 ;


    public static final String  AUTO_TEST_DATA="auto_test_data";
    public static final String  AUTO_TEST_CIRCLE = "auto_test_circle" ;
    public static final String  SELL_LINE_OR_RECYCLE_OPEN_RESULT = "sell_line_recycle_open_result" ;

    public static final String  INPUT_EDITOR_TITLE = "input_editor_title";

    public static final String  INPUT_EDITOR_CONTENT= "input_editor_content";

    public static final String  CHECKED_VALUE= "checked_value";

    public static final String  OPERATOR_ACTION_TITLE ="operator_action_title" ;
    public static final String  OPERATOR_ACTION_CONTENT ="operator_action_content" ;
    public static final String  OPERATOR_ACTION_NUMBER ="operator_action_number" ;

    public static final String  Operator_Machine_Rom_Type = "Opeartor_Machine_Rom_Type" ;

    public static final String  OPERATOR_ACTION_CHANNEL_NUMBER = "operator_action_channel_number" ;

    public static final String OPERATOR_SELECT_TIME = "operator_select_time";
    public static final String  OPERATOR_SELECT_DATE_TIME_LIST = "operator_select_time_list" ;

    public static final String OPERATOR_OFF_LINE_LIST ="operator_off_line_list";



    public static final String  RECHARGE_DESCRIPTION_TYPE= "recharge_description_type" ;
    public static final String  RECHARGE_DESCRIPTION_POSITION= "recharge_description_position" ;


    //循环售线桶的个数
    public static final String  OPERATOR_SALE_LINE_BARRAL_COUNT = "Operator_sale_line_barrel_count";

    //输入源 类型
    public static final String OPERATOR_INPUT_KEYBOARD_TYPE ="modify_input_keyboard_type" ;

    //打开回收仓的方式
    public static final String RECYCLE_OPEN_TYPE = "recycle_open_type";


    //起始选择出仓的编号
    public static final int  OPERATOR_CHANNEL_POSITION_START =  10001;
    //结束选择出仓的编号
    public static final int  OPERATOR_CHANNEL_POSITION_END =  10002;
    //返回选择仓道的编号
    public static final int  OPERATOR_CHANNEL_POSITION_NUMBER =  10003;
    //修改维护后台仓道数量请求码
    public static final int  OPERATOR_CHANNEL_MODIFY_COUNT =  10004;

    //选择仓道的位置
    public static final int  OPERATOR_CHANNEL_SELECT_POSITION =  10005;
    //Input 输入值
    public static final int  EDITOR_INPUT_DIAL_RESULT =  10006;



    //选择时间
    public static final int  OPERATOR_DATA_TIME_SELECT_REQUEST =  10007;
    public static final int  OPERATOR_DATE_TIME_RESULT = 10008 ;

    //维护后台发送指令 请求码
    public static final int  OPERATOR_SEND_COMMAND_DATA = 20001 ;

    public static final int  OPERATOR_SEND_COMMAND = 20001 ;

    public static final int  OPERATOR_MODIFY_COUNT =20003 ;

    //维护后台清空回收仓结果
    public static final int  OPERATOR_CLEAN_RECYCLE_COUNT_REQUEST = 20004 ;
    public static final int  OPERATOR_CLEAN_RECYCLE_COUNT_RESPOND= 20005 ;

    //选择串口节点
    public static final int  OPERATOR_SEND_SELECT_SERIAL_PORT_REQUEST= 20006 ;
    //选择波特率
    public static final int  OPERATOR_SEND_SELECT_BAUD_REQUEST= 20007 ;
    //选择通道ID
    public static final int  OPERATOR_SEND_SELECT_CHANNEL_REQUEST= 20008 ;
    //测试工具串口发送指令 请求码
    public static final int  OPERATOR_SEND_SERIAL_PORT_COMMAND_DATA = 20009 ;
    // 新版本移除直接保存流的形式
    public static final String  SYSTEM_ARGUS_PARAMS ="system_argus_params" ;
    //新版本保存json 模式
    public static final String  SYSTEM_ARGUS_PARAMS_JSON ="system_argus_params_json" ;

    public static final String  Ld_AD_LIST_DATA ="ld_ad_list_data" ;

    public static final String  SYSTEM_AD_LIST_DATA ="system_ad_list_data" ;

    public static final String ADVERTISE_BOTTOM_REDIRECT_URL = "advertise_bottom_redirect_url" ;
    public static final String ADVERTISE_BOTTOM_REDIRECT_TITLE = "advertise_bottom_redirect_title";
    public static final String ADVERTISE_BOTTOM_REDIRECT_TIME = "advertise_bottom_redirect_time";
    public static final String ADVERTISE_BOTTOM_REDIRECT_TYPE = "advertise_bottom_redirect_type";
    public static final int ADVERTISE_BOTTOM_REDIRECT_TYPE_URL = 0;
    public static final int ADVERTISE_BOTTOM_REDIRECT_TYPE_IMG = 1;

    // 表示 app第一次启动 需要 进工厂模式
    public static final String  LOGIN_PRODUCT_MACHINE_MODE = "login_product_machine_mode" ;

    public static final String  OPERATOR_RESULT_ANALYSIS = "operator_result_analysis" ;


    public static final String  OPERATOR_ACTION_ANALYSIS_TYPE = "operator_action_analysis_type" ;
    public static final String  OPERATOR_ACTION_STAT_TYPE = "operator_action_stat_type";

    public static final String  OPERATOR_ADVERTISE_CHANNEL_LAYOUT ="operator_advertise_channel_layout" ;

    public static final String  OPERATOR_CHANNEL_POSITION_INFO = "operator_channel_position_info" ;

    public static final String  OPERATOR_SUMMARY_CODE_DETAIL_INFO = "operator_summary_code_detail_info" ;
    //记录app启动状态及crash 数据
    public static final String  APP_START_INIT_TYPE = "app_start_init_type" ;
    // 登录类型 1=正常登录 2=断网登录 3=断电登录 4 app异常重启登陆
    public static int appNormalStatus  = 1 ;
    public static int appDisconnectStatus  = 2 ;
    public static int appPowerOnStatus = 3 ;
    public static int appCrashStatus = 4 ;

    public static String exceptionCrashJump = "crash_jump" ;

    public static final String  WAN_DA_ADVERTISE_ACTION_TYPE = "actionType" ;
    public static final  String SCAN_UI_TYPE= "scan_ui_type" ;
    /// ========== 以下是与万达版本消息交互 ==========
    public final static String WANDA_ACTION_COMMUNICATION = "com.wanda.screen.communication";
    public final static String WANDA_PACKAGE_NAME = "com.wanda.screen";
    public final static String LD_WD_MESSAGE_NAME = "message";
    public final static String LD_WD_TYPE_NAME = "type";

    public final static String FACTORY_CDB_CIRCLE_COUNT = "factory_cdb_circle_count";
    /**
     * 来电 --> 万达
     * message: needUpgrade - 来电apk询问万达APP是否可以升级
     * type: 0 - 不需要升级万达apk, 1 - 需要升级万达apk
     */
    public final static String LD_TO_WD_NEED_UPGRADE_MSG = "needUpgrade";
    public final static int LD_TO_WD_NEED_UPGRADE_TYPE_NOT_UPDATE = 0;
    public final static int LD_TO_WD_NEED_UPGRADE_TYPE_UPDATE = 1;
    /**
     * 万达 --> 来电
     * message: canUpgrade - 万达APKl回复来电app是否可以升级
     * type: 0 - 不可以升级, 1 - 可以升级
     */
    public final static String WD_TO_LD_CAN_UPGRADE_MSG = "canUpgrade";
    public final static int WD_TO_LD_CAN_UPGRADE_TYPE_NOT_UPGRADE = 0;
    public final static int WD_TO_LD_CAN_UPGRADE_TYPE_UPGRADE = 1;
    /**
     * 来电 --> 万达
     * message: InstallStatus - 来电apk返回给万达apk
     * type: 0 - 表示安装失败， 1 - 表示安装成功， 2 - 表示开始安装，3 - 正在安装
     * 安装过程中使用静默安装，因此一旦开始安装，apk收不到对应的消息
     */
    public final static String LD_TO_WD_INSTALL_STATUS_MSG = "InstallStatus";
    public final static int LD_TO_WD_INSTALL_STATUS_TYPE_FAIL = 0;
    public final static int LD_TO_WD_INSTALL_STATUS_TYPE_SUCCESS = 1;
    public final static int LD_TO_WD_INSTALL_STATUS_START = 2;
    public final static int LD_TO_WD_INSTALL_STATUS_UPDATING = 3;

    /**
     * 万达 --> 来电
     * message: upgradeStatus - 万达app返回给来电app升级信息
     * type: 0 - 升级失败， 1 - 升级成功
     */
    public final static String WD_TO_LD_UPGRADE_STATUS_MSG = "upgradeStatus";
    public final static int WD_TO_LD_UPGRADE_STATUS_TYPE_FAIL = 0;
    public final static int WD_TO_LD_UPGRADE_STATUS_TYPE_SUCCESS = 1;
    /**
     * 万达app版本
     */
    public final static String APP_VERSION_MSG = "appVersion";
    /**
     * 联接状态
     */
    public final static String CONNECT_STATUS_MSG = "connectStatus";
    /// =================================================

    public static final String DATE_TIME_PICKER_MINIMUMDATE = "date_time_picker_minimumdate";
    public static final String DATE_TIME_PICKER_MAXIMUMDATE = "date_time_picker_maximumdate";


//    public static final String  KILL_APP_PROJECT_NAME = "com.cdtv.yulindevice" ;//"com.mobilepower.tong" ;
//    public static final String  KILL_APP_PROJECT_NAME_ACTIVITY = "com.cdtv.activity.yitiji.CultureDesktopActivity" ;//"com.mobilepower.tong.ui.activity.WelcomeActivity" ;
//    public static final String  BOAO_ORG_WEB_URL = "http://srv.boaohub.org/" ;

    /// 提示用户已取走消息时长
    public static final long PROMPT_USER_POWER_BANK_HAS_BEEN_TAKEN_DELAY = 10000;


    /// map 地图 发送消息  飞凡 -> 来电
    public static final String  MAP_NAVIGATION_ACTION_COMMUNICATION = "map_navigation_action_communication" ;

    /// map 地图  来电  ->  飞凡
    public static final String  MAP_NAVIGATION_ACTION_COMMUNICATION_DH = "map_navigation_action_communication_dh";

    //发送消息 event
    public static final String MAP_NAVIGATION_ACTION_EVENT = "map_navigation_action_event";

    // 借动作
    public static final String MAP_NAVIGATION_ACTION_BORROW= "borrow" ;
    //还动作
    public static final String MAP_NAVIGATION_ACTION_RETURN ="return" ;
    // 退出动作
    public static final String MAP_NAVIGATION_ACTION_QUITE = "quite" ;
    // 机器编号 action
    public static final String MAP_NAVIGATION_ACTION_TERMINAL_ID = "terminalId" ;

    //机器信息 机器编号详情
    public static final String MAP_NAVIGATION_TERMINAL_INFO= "terminal_info" ;




    // faq 错误  统一
    //机器串口错误
    public static final int machineError = 1;
    // 升级任务
    public static final int machineUpdatePackage = 2;
    // 断网
    public static final int netDisconnect = 3;
    // 扫码类型更改
    public static final int updateChargeDescribe = 4;
    // 串口拉死
    public static final int serialDataError = 5;
    // apk 检测错误
    public static final int apkCheckError = 6;
    // 检测开关机
    public static final int powerOffCheck = 7 ;
    // 除主apk 其他apk升级
    public static final int upgradeOtherApk = 8 ;
    // 临时版本超时
    public static final int invalidErrorTempTime = 9;

    //远程操控机器升级
    public static final int  remoteControlMachine = 10;


    // apk 检测crash 多次
    public static final int apkCheckCrash = 11;

    //远程操控机器升级
    public static final int  smkWarming = 12;

    public static final  int ApkInstall = 10000;
    public static final  int ChannelInstall = 10001;
    public static final  int MainCtrlInstall = 10002;
    public static final  int SaleCtrlInstall = 10003;

    //记录仓管app 存储列表 数据
    public static final String  STORE_HOUSE_CDB_LIST_INFO = "store_house_cdb_list_info" ;

}
