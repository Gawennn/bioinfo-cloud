package com.bioinfo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.bioinfo.mapper")
@SpringBootApplication
public class BioinfoCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(BioinfoCloudApplication.class, args);
    }

}
