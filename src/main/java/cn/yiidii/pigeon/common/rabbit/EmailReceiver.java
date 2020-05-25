package cn.yiidii.pigeon.common.rabbit;

import cn.yiidii.pigeon.base.RabbitConstant;
import cn.yiidii.pigeon.common.mail.dto.MailBean;
import cn.yiidii.pigeon.common.util.MailUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RabbitListener(queues = RabbitConstant.EMAIL_QUEUE)
public class EmailReceiver {

    @RabbitHandler
    public void sendEmail(MailBean mail, Channel channel, Message message) throws IOException {
        log.info("receive a mail : {} ", mail);
        try {
            MailUtil.sendMail(mail);
        } catch (Exception e) {
            //异常手动确认
            log.info("EmailReceiver ({}) exception: {}", mail, e);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
