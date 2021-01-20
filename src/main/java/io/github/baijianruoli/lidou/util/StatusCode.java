package io.github.baijianruoli.lidou.util;

public enum StatusCode {
    Success(0, "成功"),
    Fail(1, "失败"),
    ;

    private Integer code;

    StatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
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

    private String msg;

}
