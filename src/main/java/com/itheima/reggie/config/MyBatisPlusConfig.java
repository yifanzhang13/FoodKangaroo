package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置MP的分页插件
 */
@Configuration
public class MyBatisPlusConfig {
    //创建容器，交给spring管理
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        // 1.定义拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 2.添加具体的拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
