package io.github.baijianruoli.lidou.util;/**
 * Created by Administrator on 2019/7/29.
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的响应封装类
 * @Author:debug (SteadyJack)
 * @Date: 2019/7/29 14:33
 **/
@Data
public class BaseResponse<T>  implements Serializable {

    private Integer code;
    private String msg;

    private T data;

     public BaseResponse()
     {

     }
    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
    }

    public BaseResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(StatusCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
        this.data = data;
    }

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}