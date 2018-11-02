package com.controller.common;

import com.common.Const;
import com.pojo.User;
import com.util.CookieUtil;
import com.util.JsonUtil;
import com.util.RedisShardedPoolUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        //获取sessionId
        String loginToken = CookieUtil.readLoginToken(request);
        //从redis中获取user
        String userStr = RedisShardedPoolUtil.get(loginToken);
        //转换成user对象
        User user = JsonUtil.string2Obj(userStr, User.class);
        if (user!=null){
            RedisShardedPoolUtil.expire(loginToken, Const.RedisCache.REDIS_SESSION_TIME);
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
