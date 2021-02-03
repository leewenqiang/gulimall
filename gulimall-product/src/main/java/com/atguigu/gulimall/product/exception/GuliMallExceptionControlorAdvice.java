package com.atguigu.gulimall.product.exception;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName GuliMallControlorAdvice
 * @Description 统一处理异常
 * @Author lwq
 * @Date 2020/12/19 14:41
 * @Version 1.0
 */

@Slf4j
@RestControllerAdvice(basePackages = {"com.atguigu.gulimall.product.controller"})
public class GuliMallExceptionControlorAdvice {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){

        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = new HashMap<>(bindingResult.getErrorCount());
        bindingResult.getFieldErrors().forEach(item-> {
            //错误提示消息(配置了就取配置的 不配置就默认的
            String errorMessage = item.getDefaultMessage();
            //错误字段
            String errorField = item.getField();
            errorMap.put(errorField, errorMessage);
        });

        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMsg()).put("error",errorMap);
    }

    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误"+throwable);
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }

}
