package com.colorful.spring.boot.ratelimit.handler;

import com.colorful.spring.boot.ratelimit.exception.RateLimitInvocationException;
import com.colorful.spring.boot.ratelimit.util.RequestUtils;

/**
 * @author hby
 * 2020/7/28 - 10:26.
 **/
public class DefaultLimitKeyHandler  implements LimitKeyHandler{
    /**
     * 使用用户为key 必须重写
     * 否则抛出异常
     * @return
     */
    @Override
    public String getUserKey() {
        throw new RateLimitInvocationException();
    }

    @Override
    public String getIpKey() {
        return RequestUtils.getReqIp();
    }
}
