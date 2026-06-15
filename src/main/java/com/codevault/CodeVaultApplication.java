package com.codevault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.codevault.mapper")
public class CodeVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeVaultApplication.class, args);
        System.out.println("========================================");
        System.out.println("  CodeVault 代码金库系统启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
