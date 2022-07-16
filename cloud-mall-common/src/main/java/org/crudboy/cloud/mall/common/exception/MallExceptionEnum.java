package org.crudboy.cloud.mall.common.exception;

/**
 * 错误类型枚举类
 */
public enum MallExceptionEnum {
    NEED_USER_NAME(10001, "用户名不能为空"),

    NEED_PASSWORD(10002, "密码不能为空"),

    PASSWORD_TO_SHORT(10003, "密码长度不小于8位"),

    NAME_EXISTED(10004, "不允许重名"),

    INSERT_FAILED(10005, "新增失败"),

    WRONG_PASSWORD(10006, "密码错误"),

    NEED_LOGIN(10007, "用户未登录"),

    UPDATE_FAILED(10008, "更新失败"),

    NEED_ADMIN(10009, "无管理员权限"),

    PRAM_NOT_NULL(10010, "参数不能为空"),

    REQUEST_PRAM_ERROR(10011, "请求参数错误"),

    DELETE_FAILED(10012, "删除失败"),

    MKDIR_FAILED(10013, "文件夹创建失败"),

    UPLOAD_FILE_FAILED(10014, "文件上失败"),

    PRODUCT_DISABLE(10015, "商品不可用"),

    NOT_ENOUGH(10016, "商品库存不足"),

    CART_EMPTY(10017, "购物车为空"),

    NOT_ENUM(10018, "未找到枚举类"),

    NOT_ORDER(10019, "订单不存在"),

    ORDER_MISMATCH(10019, "订单不匹配"),

    WRONG_ORDER_STATUS(10020, "订单状态不符"),

    LOGIN_REPEATED(10021, "禁止重复登录"),

    SYSTEM_ERROR(20000, "系统异常"),

    NETWORK_ERROR(20001, "网络异常");


    Integer code;

    String msg;

    MallExceptionEnum(Integer code, String msg) {
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
}
