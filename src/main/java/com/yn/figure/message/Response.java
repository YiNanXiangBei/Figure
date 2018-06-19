package com.yn.figure.message;

import java.io.Serializable;

/**
 * @author yinan
 */
public class Response implements Serializable{

    private static final long serialVersionUID = 3856341330476353804L;
    /**
     * 返回状态码
     */
    private int code;

    /**
     * 返回状态信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Response(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
