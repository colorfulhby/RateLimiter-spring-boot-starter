package com.colorful.spring.boot.ratelimit.exception;

/**
 * @author hby
 * 2020/7/24 - 16:58.
 **/
public class KlockInvocationException extends RuntimeException {

    public KlockInvocationException() {
    }

    public KlockInvocationException(String message) {
        super(message);
    }

    public KlockInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
