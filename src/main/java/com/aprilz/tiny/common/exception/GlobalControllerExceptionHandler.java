package com.aprilz.tiny.common.exception;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 全局异常异常处理
 *
 * @author aprilz
 */
@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    /**
     * 如果超过长度，则前后段交互体验不佳，使用默认错误消息
     */
    static Integer MAX_LENGTH = 200;

    /**
     * 自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public CommonResult<Object> handleServiceException(HttpServletRequest request, final Exception e, HttpServletResponse response) {


        //如果是自定义异常，则获取异常，返回自定义错误消息
        if (e instanceof ServiceException) {
            ServiceException serviceException = ((ServiceException) e);
            ResultCode resultCode = serviceException.getResultCode();

            Integer code = null;
            String message = null;

            if (resultCode != null) {
                code = resultCode.code();
                message = resultCode.message();
            }
            //如果有扩展消息，则输出异常中，跟随补充异常
            if (!serviceException.getMsg().equals(ServiceException.DEFAULT_MESSAGE)) {
                message += ":" + serviceException.getMsg();
            }
            log.error("全局异常[ServiceException]:{}-{}", serviceException.getResultCode().code(), serviceException.getResultCode().message(), e);
            return CommonResult.error(code, message);
        } else {
            log.error("全局异常[ServiceException]:", e);
        }

        //默认错误消息
        String errorMsg = "服务器异常，请稍后重试";
        if (e != null && e.getMessage() != null && e.getMessage().length() < MAX_LENGTH) {
            errorMsg = e.getMessage();
        }
        return CommonResult.error(ResultCode.FAILED.code(), errorMsg);
    }

    /**
     * @description http请求参数转换异常
     **/
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public CommonResult messageExceptionHandler(HttpMessageNotReadableException e) {
        log.warn("http请求参数转换异常: " + e.getMessage());
        return CommonResult.error(ResultCode.PARAMS_ERROR);
    }

    /**
     * bean校验未通过异常
     *
     * @see javax.validation.Valid
     * @see org.springframework.validation.Validator
     * @see org.springframework.validation.DataBinder
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResult<Object> validExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {

        BindException exception = (BindException) e;
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            return CommonResult.error(ResultCode.PARAMS_ERROR.code(), error.getDefaultMessage());
        }
        return CommonResult.error(ResultCode.PARAMS_ERROR);
    }

    /**
     * bean校验未通过异常
     *
     * @see javax.validation.Valid
     * @see org.springframework.validation.Validator
     * @see org.springframework.validation.DataBinder
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CommonResult<Object> constraintViolationExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        ConstraintViolationException exception = (ConstraintViolationException) e;
        return CommonResult.error(ResultCode.PARAMS_ERROR.code(), exception.getMessage());
    }


    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult<Object> handleException(AccessDeniedException exception) {
        String message = exception.getLocalizedMessage();
        log.error("全局异常捕获AccessDeniedException：{}", message);
        return CommonResult.error(ResultCode.FORBIDDEN);
    }

    @ExceptionHandler(LockedException.class)
    public CommonResult<Object> handleException(LockedException exception) {
        String message = exception.getLocalizedMessage();
        log.error("全局异常捕获LockedException：{}", message);
        return CommonResult.error(ResultCode.ADMIN_LOCKED_ACCOUNT);
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public CommonResult<Object> runtimeExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        log.error("全局异常[RuntimeException]:", e);
        return CommonResult.error(ResultCode.FAILED);
    }
}
