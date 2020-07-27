package com.colorful.spring.boot.ratelimit.service.impl;


import com.colorful.spring.boot.ratelimit.handler.LimitKeyHandler;
import com.colorful.spring.boot.ratelimit.service.LimitKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * @author hby
 * 2020/7/24 - 16:26.
 **/
public class LimitKeyServiceImpl implements LimitKeyService {


    @Autowired
    private LimitKeyHandler limitKeyHandler;

    @Override
    public String getUserKey() {

        return "hby";
    }

    @Override
    public String getIpKey() {

        return "10.82.1.2";
    }

}