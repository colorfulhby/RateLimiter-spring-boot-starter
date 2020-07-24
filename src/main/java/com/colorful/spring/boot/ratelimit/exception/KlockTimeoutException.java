package com.colorful.spring.boot.ratelimit.exception;

/**
 * @author hby
 * 2020/7/24 - 16:58.
 **/
public class KlockTimeoutException extends RuntimeException {

    public KlockTimeoutException() {
    }

    public KlockTimeoutException(String message) {
        super(message);
    }

    public KlockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
