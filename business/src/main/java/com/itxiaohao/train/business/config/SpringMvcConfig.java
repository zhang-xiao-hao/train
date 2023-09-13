package com.itxiaohao.train.business.config;

import com.itxiaohao.train.common.interceptor.LogInterceptor;
import com.itxiaohao.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

   @Resource
   LogInterceptor logInterceptor;
   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      //写在前面的先生效
      registry.addInterceptor(logInterceptor);

      registry.addInterceptor(memberInterceptor)
              .addPathPatterns("/**");
   }
}
