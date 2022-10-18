package com.aprilz.tiny.common.api;

/**
 * 枚举了一些常用API操作码
 * Created by aprilz on 2019/4/19.
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "服务器异常，请稍后重试"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(501, "请登录"),
    FORBIDDEN(506, "抱歉，您没有访问权限"),
    LIMIT_ERROR(507, "访问过于频繁，请稍后再试"),
    ADMIN_INVALID_ACCOUNT(605,"账号或密码错误"),
    ADMIN_LOCKED_ACCOUNT(606,"用户帐号已锁定不可用"),
    ADMIN_INVALID_KAPTCHA(607,"验证码错误"),
    ADMIN_NAME_EXIST(608,"管理员已经存在"),
    ADMIN_DELETE_NOT_ALLOWED(609,"管理员不能删除自己账号"),
    AFTERSALE_NOT_EXIST(610,"售后不存在"),
    AFTERSALE_NOT_ALLOWED(611,"售后不能进行审核通过操作"),
    AFTERSALE_NOT_ALLOWED_REFUND(611,"售后不能进行退款操作"),
    ORDER_REFUND_FAILED(612,"订单退款失败"),

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
