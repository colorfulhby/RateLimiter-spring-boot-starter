package com.colorful.spring.boot.ratelimit.exception;

/**
 * @author hby
 * 2020/7/24 - 16:58.
 **/
public class RateLimitInvocationException extends RuntimeException {

    public RateLimitInvocationException() {
    }

    public RateLimitInvocationException(String message) {
        super(message);
    }

    public RateLimitInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
