package com.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mall.module.**.mapper")
public class MallApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
        System.out.println("========================================");
        System.out.println("  在线商城系统已启动！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
