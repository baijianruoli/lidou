package com.zut.lpf.lidou.util;

public enum StatusCode {
    Success(0,"成功"),
    Fail(1,"失败"),
    InvalidParams(201,"非法的参数!"),
    UserNotLogin(202,"用户没登录"),

    UnknownError(500,"未知异常，请联系管理员!"),
    InvalidCode(501,"验证码不正确!"),
    AccountPasswordNotMatch(502,"账号密码不匹配!"),
    AccountHasBeenLocked(503,"账号已被锁定,请联系管理员!"),
    AccountValidateFail(504,"账户验证失败!"),

    CurrUserHasNotPermission(505,"当前用户没有权限访问该资源或者操作！"),

    PasswordCanNotBlank(1000,"密码不能为空!"),
    OldPasswordNotMatch(1001,"原密码不正确!"),
    UpdatePasswordFail(1002,"修改密码失败~请联系管理员!"),

    SysUserCanNotBeDelete(1003,"超级系统管理员不能被删除!"),
    CurrUserCanNotBeDelete(1004,"当前用户不能删除自己!"),

    DeptHasSubDeptCanNotBeDelete(2001,"该部门下包含有子部门，请先删除子部门!"),

    SpecificMenuCanNotBeDelete(3001,"指定的菜单不能被删除，要删除就手动数据库删除~fuck you!"),

    PostCodeHasExist(4001,"岗位编码已存在!"),

    MenuHasSubMenuListCanNotDelete(5001,"该菜单下有子菜单，请先删除子菜单！"),

    SysUserAndCurrUserCanNotResetPsd(6001,"超级系统管理员与自己不能重置密码！"),

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

    private  String msg;

}
