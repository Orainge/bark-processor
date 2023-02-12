package com.orainge.bark_processor.server.exception;

/**
 * 时间间隔太短
 */
public class TimeIntervalToShortException extends RuntimeException{
    public TimeIntervalToShortException(){
        super("请求时间间隔太短");
    }
}
