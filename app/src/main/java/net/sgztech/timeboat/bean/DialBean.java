package net.sgztech.timeboat.bean;

/**
 * 本地表盘的bean
 */
public class DialBean {

    /**
     * 图片地址 drawable目录下的图片
     */
    private int resourceId;

    /**
     * 是否选中
     */
    private boolean isChecked;

    public DialBean() {
    }


    public DialBean(int resourceId, boolean isChecked) {
        this.resourceId = resourceId;
        this.isChecked = isChecked;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
