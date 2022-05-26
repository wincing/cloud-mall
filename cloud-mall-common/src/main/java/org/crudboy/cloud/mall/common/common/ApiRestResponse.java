package org.crudboy.cloud.mall.common.common;

import org.crudboy.cloud.mall.common.exception.MallExceptionEnum;

/**
 * 通用响应对象
 */
public class ApiRestResponse<T> {

    private Integer code;
    private String msg;
    private  T data;

    private static final int OK_CODE = 10000;
    private static final String OK_MSG = "SUCCESS";

    public ApiRestResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ApiRestResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiRestResponse() {
        this(OK_CODE, OK_MSG);
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    /**
     * 成功处理请求的返回结果
     * @param result 封装在响应中的数据
     */
    public static <T> ApiRestResponse success(T result) {
        ApiRestResponse<T> response = new ApiRestResponse<>();
        response.setData(result);
        return response;
    }

    /**
     * 处理请求失败的返回对象
     * @param code 错误码
     * @param msg 错误信息
     */
    public static <T> ApiRestResponse error(Integer code, String msg) {
        return new ApiRestResponse(code, msg);
    }

    public static <T> ApiRestResponse error(MallExceptionEnum ex) {
        return new ApiRestResponse(ex.getCode(), ex.getMsg());
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

    @Override
    public String toString() {
        return "ApiRestResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
