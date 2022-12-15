
package com.imlaidian.ldclog;

class SendAction {

    long fileSize; //文件大小

    LdUploadItem uploadItem;


    SendLogRunnable sendLogRunnable;

    boolean isValid() {
        boolean valid = false;
        if (sendLogRunnable != null) {
            valid = true;
        } else if (fileSize > 0) {
            valid = true;
        }
        return valid;
    }
}
