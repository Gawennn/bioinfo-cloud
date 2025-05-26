package com.bioinfo.mq;

import com.alibaba.fastjson.JSON;
import com.bioinfo.mapper.UserMapper;
import com.bioinfo.mq.messageEntity.EmailTaskMessage;
import com.bioinfo.thread.MailThreadPool;
import com.bioinfo.utils.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author 刘家雯
 * @Date 2025/5/16
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "email-task-topic", consumerGroup = "email-consumer-group")
public class EmailConsumer implements RocketMQListener<String> {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void onMessage(String message) {

        EmailTaskMessage emailTaskMessage = JSON.parseObject(message, EmailTaskMessage.class);

        MailThreadPool.getEmailSenderPool().submit(() -> {
            try {
                String to = userMapper.getEmailById(emailTaskMessage.getUserId());
                File attachment = new File(emailTaskMessage.getResultPath());
                mailService.sendAttachmentMail(to, "分析结果", "您好，分析结果已完成，请查收附件", attachment);
                log.info("邮件发送成功至 {}", to);
            } catch (Exception e) {
                log.error("邮件发送失败", e);
            }
        });
    }
}

