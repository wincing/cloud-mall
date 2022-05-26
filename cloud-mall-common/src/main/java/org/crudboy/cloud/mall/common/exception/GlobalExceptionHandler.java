package org.crudboy.cloud.mall.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.crudboy.cloud.mall.common.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理类
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiRestResponse handleSystemException(Exception e) {
        log.error("Default Exception: ", e);
        return ApiRestResponse.error(MallExceptionEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(MallException.class)
    public ApiRestResponse handleBusinessException(MallException e) {
        log.error("MallException: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);

        BindingResult result = e.getBindingResult();
        List<String> errorList = new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError error : allErrors) {
                errorList.add(error.getDefaultMessage());
            }
        }

        if (errorList.size() == 0) {
            return ApiRestResponse.error(MallExceptionEnum.REQUEST_PRAM_ERROR);
        }

        return ApiRestResponse.error(MallExceptionEnum.REQUEST_PRAM_ERROR.getCode(),
                errorList.toString());
    }

}
