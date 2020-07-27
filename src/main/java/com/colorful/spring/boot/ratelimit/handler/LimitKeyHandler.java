package com.colorful.spring.boot.ratelimit.handler;

/**
 * @author hby
 * 2020/7/27 - 10:26.
 **/
public interface LimitKeyHandler {

    /**
     * 获取用户唯一key
     * @return
     */
    String getUserKey();


    /**
     * 获取 ip
     * @return
     */
    String getIpKey();
}

