package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//Slf4j输出日志，方便调试，由Lombok提供
@Slf4j
@ServletComponentScan //扫描过滤器组件
@SpringBootApplication
@EnableTransactionManagement//开启事务支持，因为DishService中的saveWithFlavor方法涉及到多张表的操作
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class);
        log.info("项目启动成功...");
    }
}
