package org.crudboy.cloud.mall.common.exception;

/**
 * 自定义异常类
 */
public class MallException extends RuntimeException {
    private final Integer code;
    private final String msg;

    public MallException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public MallException(MallExceptionEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "{\n" +
                "    \"code\": \"" + code + "\",\n" +
                "    \"msg\": \"" + msg + "\"\n" +
                "}";
    }
}
