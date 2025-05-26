package com.bioinfo.config;

import com.bioinfo.utils.LoginInterceptor;
import com.bioinfo.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/10
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登陆拦截器
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                        "/file/**",
                        "/user/deposit",
                        "/analysis/alpha/**",
                        "/analysis/beta/**",
                        "/admin/voucher/seckill/{voucherId}",
                        "/discuss/*"
                ).order(1);

        // token刷新拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }
}
