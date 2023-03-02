package vip.huancaixi.video.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import vip.huancaixi.video.config.RabbitMQConfig;

import javax.annotation.Resource;

@Service
@Slf4j
public class MQSender {

    @Resource
    RabbitTemplate rabbitTemplate;

    public void videoMessage(String message){
        log.info("发送信息"+message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE,"video.message",message);
    }
}
