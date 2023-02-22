package com.orainge.bark_processor.server.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Result {
    private int code;
    private String message;
    private long timestamp = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));

    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static Result error() {
        Result result = new Result();
        result.setCode(500);
        result.setMessage("error");
        return result;
    }
}
