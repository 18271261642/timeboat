package net.sgztech.timeboat.bean;

/**
 * 本地表盘的bean
 * @author Admin
 */
public class DialBean {

    /**
     * 图片地址 drawable目录下的图片
     */
    private int resourceId;

    /**
     * 表盘id
     */
    private int dialId;


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

    public int getDialId() {
        return dialId;
    }

    public void setDialId(int dialId) {
        this.dialId = dialId;
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
