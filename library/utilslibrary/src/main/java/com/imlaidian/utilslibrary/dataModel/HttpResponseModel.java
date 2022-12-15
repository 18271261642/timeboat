package com.imlaidian.utilslibrary.dataModel;


public class HttpResponseModel<T> {

    private T      data;          //网络请求返回数据
    private String msg="";          //网络消息返回
    private int  code = -1;
    private int  result =-1 ;

    public HttpResponseModel(){

    }

    public HttpResponseModel( int result ,int code,String msg , T data) {
        this.data =data ;
        this.result = result ;
        this.msg = msg ;
        this.code =code ;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean isSuccess(){
        return  result ==1 ;
    }

    public boolean isTokeInvalid(){
        return  result ==-4 ;
    }

    @Override
    public String toString() {
        return "HttpResponseModel [data=" + data + ", msg=" + msg + ", code="
                + code + "]";
    }


}
