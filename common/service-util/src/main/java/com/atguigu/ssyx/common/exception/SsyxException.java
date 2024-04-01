package com.atguigu.ssyx.common.exception;

import com.atguigu.ssyx.common.result.ResultCodeEnum;
import lombok.Data;
/**
 * 自定义异常类
 */
@Data
public class SsyxException extends RuntimeException{
    private Integer code;

    public SsyxException(Integer code, String msg) {
        super(msg);
        this.code = code;

    }
    public SsyxException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

}
