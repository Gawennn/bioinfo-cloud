package com.bioinfo.dto;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    // 成功响应
    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> ok(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 失败响应
    public static <T> Result<T> fail(String message) {
        return fail(400, message);
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 未授权响应
    public static <T> Result<T> unauthorized(String message) {
        return fail(401, message);
    }

    // 禁止访问响应
    public static <T> Result<T> forbidden(String message) {
        return fail(403, message);
    }

    // 资源未找到响应
    public static <T> Result<T> notFound(String message) {
        return fail(404, message);
    }

    // 服务器错误响应
    public static <T> Result<T> error(String message) {
        return fail(500, message);
    }
}
