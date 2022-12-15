package com.imlaidian.utilslibrary.thread;

/**
 * 业务优先级，priority越小，优先级越高
 */
public enum BusinessPriority {
    UI(0),//UI界面业务

    NORMAL(UI.value() + 1),//普通业务

    NETWORK(NORMAL.value() + 1),//网络业务

    THIRD_PARTY(NETWORK.value() + 1),//第三方业务

    DOWNLOAD(THIRD_PARTY.value() + 1);//下载业务

    private int priority;

    BusinessPriority(int priority) {
        this.priority = priority;
    }

    public int value() {
        return priority;
    }
}
