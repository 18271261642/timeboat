
package com.imlaidian.ldclog;

class LdLogModel {

    enum Action {
        WRITE, SEND, FLUSH
    }

    Action action;

    WriteAction writeAction;

    SendAction sendAction;

    boolean isValid() {
        boolean valid = false;
        if (action != null) {
            if (action == Action.SEND && sendAction != null && sendAction.isValid()) {
                valid = true;
            } else if (action == Action.WRITE && writeAction != null && writeAction.isValid()) {
                valid = true;
            } else if (action == Action.FLUSH) {
                valid = true;
            }
        }
        return valid;
    }
}
