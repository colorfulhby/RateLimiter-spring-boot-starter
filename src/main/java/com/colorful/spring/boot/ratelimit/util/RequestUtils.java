package com.colorful.spring.boot.ratelimit.util;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hby
 * 2020/7/24 - 20:11.
 **/
public class RequestUtils {

    /**
     * 获取客户端IP
     * @return IP
     */
    public static String getReqIp(){
        String ip = null;
        HttpServletRequest request = getRequest();
        if(null != request){
            //ip = HttpUtil.getClientIP(request);
        }
        return ip;
    }

    /**
     * 获取用户名，注意配合实际项目使用
     * @return String
     */
    public static String getReqUserName(){
        HttpServletRequest request = getRequest();
        if(null == request){
            return null;
        }
        String userName = request.getHeader("userName");
        if(StringUtils.isEmpty(userName)){
            userName = (String) request.getAttribute("userName");
        }
        return userName;
    }

    /**
     * 获取客户端IP
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(null != requestAttributes){
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
}
