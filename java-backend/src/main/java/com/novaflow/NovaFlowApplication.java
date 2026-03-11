package com.novaflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * NovaFlow 后端应用主类
 */
@SpringBootApplication
@EnableAsync
@MapperScan("com.novaflow")
public class NovaFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovaFlowApplication.class, args);
        System.out.println("""

            ======================================
               🎬 NovaFlow 后端服务启动成功
            ======================================

               📡 API地址: http://localhost:8080
               📚 文档地址: http://localhost:8080/swagger-ui.html
               💡 环境: development

            ======================================
            """);
    }
}
