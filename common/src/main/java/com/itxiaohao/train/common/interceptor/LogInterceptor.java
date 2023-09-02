package com.itxiaohao.train.common.interceptor;

import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器
 */
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 为了实现Interceptor的日志也有流水号，把日志流水号放到Interceptor（日志是通过aop实现的，aop晚于Interceptor执行）
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        return true;
    }

}
