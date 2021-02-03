package com.atguigu.common.exception;

/***
 *
 */
public enum BizCodeEnum {


    /**
     * 系统未知异常
     */
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_SMS_CODE(10002,"短信验证码频率太高，稍后再试"),
    TOO_MANY_RQUEST(10003,"请求流量过大，稍后再试"),

    /**
     * 参数校验异常
     */
    VALID_EXCEPTION(10001,"参数格式校验失败"),


    PRODUCT_UP_ERROR(11000,"商品上架异常"),

    USER_EXIST_EXCEPTION(150001,"用户名已存在"),
    PHONE_EXIST_EXCEPTION(150002,"手机号已存在"),
    LOGIN_AACCOUNT_VALID_EXCEPTION(15003,"账号密码错误"),

    NO_STOCK_EXCEPTION(21000,"商品库存不足");




    private int code;
    private String msg;

    BizCodeEnum(int code,String msg) {
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
