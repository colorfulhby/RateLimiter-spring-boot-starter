package com.colorful.spring.boot.ratelimit.service;

/**
 * @author hby
 * 2020/7/24 - 16:26.
 **/
public interface LimitKeyService {
    /**
     * 获取用户唯一key
     * @return
     */
    String getUserKey();

    /**
     * 获取IP
     * @return
     */
    String getIpKey();

}
