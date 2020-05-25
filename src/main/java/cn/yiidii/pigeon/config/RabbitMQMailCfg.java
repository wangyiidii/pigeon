package cn.yiidii.pigeon.config;

import cn.yiidii.pigeon.base.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

@Slf4j
public class RabbitMQMailCfg {

    /**
     * 邮件
     **/
    @Bean
    public Queue mailQueue() {
        return new Queue(RabbitConstant.EMAIL_QUEUE, true, false, false, null);
    }

    @Bean
    public Exchange mailExchange() {
        return new TopicExchange(RabbitConstant.EMAIL_EXCHANGE, true, false, null);
    }

    @Bean
    public Binding orderBinding() {
        return new Binding(RabbitConstant.EMAIL_QUEUE, Binding.DestinationType.QUEUE, RabbitConstant.EMAIL_EXCHANGE,
                RabbitConstant.EMAIL_ROUTING_KEY, null);
    }

    /**
     * json输出
     **/
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
