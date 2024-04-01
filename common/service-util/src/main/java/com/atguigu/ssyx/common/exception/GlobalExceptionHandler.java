package com.atguigu.ssyx.common.exception;

import com.atguigu.ssyx.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }
    //通过反射获取异常类的信息
    @ExceptionHandler(SsyxException.class)
    @ResponseBody
    public Result error(SsyxException ssxyException) {
        return Result.build(null, ssxyException.getCode(), ssxyException.getMessage());
    }
}
