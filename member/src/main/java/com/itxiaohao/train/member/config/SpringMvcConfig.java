package com.itxiaohao.train.member.config;

import com.itxiaohao.train.common.interceptor.LogInterceptor;
import com.itxiaohao.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
   // 日志流水号
   @Resource
   LogInterceptor logInterceptor;
   // 保存用户信息到线程本地变量的拦截器
   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       //写在前面的先生效
       registry.addInterceptor(logInterceptor);
       // 路径暴怒要包含context-path
       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/member/send-code",
                       "/member/login"
               );
   }
}
