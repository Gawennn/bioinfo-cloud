package com.bioinfo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/22
 */
@Configuration
@EnableTransactionManagement
public class MyBatisConfig {

    // 配置 Mapper 扫描
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.bioinfo.mapper"); // 设置 Mapper 接口的包路径
        return scannerConfigurer;
    }
}