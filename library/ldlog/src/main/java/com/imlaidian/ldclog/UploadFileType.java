package com.imlaidian.ldclog;

public enum UploadFileType {

    DateLogYmdFile(1),
    DateLogYmdHHFile(2),
    TerminalLog(3),
    StartApp(4),
    SerialLog(5),
    ScreenShot(6),
    HeapDump(7),
    OtherFile(-1);

    private int uploadType ;
    UploadFileType(int uploadType) {
        this.uploadType = uploadType ;
    }

    private int getValue(){
        return uploadType ;
    }

    public static UploadFileType setValue(int uploadType) {

        if (DateLogYmdFile.getValue() == uploadType) {
            return DateLogYmdFile;
        }else if(DateLogYmdHHFile.getValue() == uploadType){
            return DateLogYmdHHFile;
        }else if(TerminalLog.getValue() == uploadType){
            return TerminalLog;
        }else if(StartApp.getValue() == uploadType){
            return StartApp;
        }else if(SerialLog.getValue() == uploadType){
            return SerialLog;
        }else if(ScreenShot.getValue() == uploadType){
            return ScreenShot ;
        }else if(HeapDump.getValue() == uploadType){
            return HeapDump ;
        }else{
            return OtherFile;
        }

    }

}
