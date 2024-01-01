package com.fly.maker.meta;

/**
 * 元信息异常
 */
public class MetaException extends RuntimeException{
    public MetaException() {
        super();
    }

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
