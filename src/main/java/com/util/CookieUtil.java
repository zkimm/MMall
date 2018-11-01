package com.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {

    //一级域名下的domain
    private final static String COOKIE_DOMAIN = "mmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("read cookieName: {} cookieValue: {} ", cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void writerLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);//不能通过脚本访问cookie
        //cookie的有有效期 秒,-1表示永久，不设置，则会只保存到内存
        cookie.setMaxAge(60 * 60 * 24);//有效期为1天
        log.info("write cookieName {},cookieValue {}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    log.info("del cookieName: {} cookieValue: {} ", cookie.getName(), cookie.getValue());
                   response.addCookie(cookie);
                   return;
                }
            }
        }
    }
}
