package com.mall.config;

import com.mall.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Map<String, Object> result = new HashMap<>();
        result.put("errors", errors);
        return Result.error(400, "参数校验失败");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Map<String, Object>> handleAllExceptions(Exception ex) {
        System.out.println("=== 服务器内部错误 ===");
        System.out.println("异常类型: " + ex.getClass().getName());
        System.out.println("异常消息: " + ex.getMessage());
        ex.printStackTrace();
        return Result.error(500, "服务器内部错误: " + ex.getMessage());
    }
}