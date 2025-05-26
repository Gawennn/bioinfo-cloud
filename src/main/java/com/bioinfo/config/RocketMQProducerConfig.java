package com.bioinfo.config;

import org.springframework.beans.factory.annotation.Value;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ配置类
 *
 * @author 刘家雯
 * @Date 2025/5/15
 */
@Configuration
public class RocketMQProducerConfig {

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Bean
    public RocketMQTemplate rocketMQTemplate() {
        RocketMQTemplate template = new RocketMQTemplate();
        DefaultMQProducer producer = new DefaultMQProducer("default-producer-group");
        producer.setNamesrvAddr(nameServer);
        template.setProducer(producer);
        return template;
    }
}
