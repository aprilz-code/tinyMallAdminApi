package com.aprilz.tiny.common.api;

/**
 * 枚举了一些常用API操作码
 * Created by aprilz on 2019/4/19.
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "服务器异常，请稍后重试"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "抱歉，您没有访问权限"),
    LIMIT_ERROR(1003, "访问过于频繁，请稍后再试"),
    ADMIN_INVALID_ACCOUNT(605,"账号或密码错误"),
    ADMIN_LOCKED_ACCOUNT(606,"用户帐号已锁定不可用"),
    ADMIN_INVALID_KAPTCHA(607,"验证码错误"),
    /**
     * 参数异常
     */
    PARAMS_ERROR(4002, "参数异常");
    private Integer code;
    private String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}
