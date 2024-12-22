package com.tripleseven.orderapi.common.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

public class RabbitConfig {
    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";

    private static final String ORDER_QUEUE_NAME = "nhn24.order.queue";
    private static final String POINT_QUEUE_NAME = "nhn24.point.queue";
    private static final String CART_QUEUE_NAME = "nhn24.cart.queue";

    private static final String ORDER_ROUTING_KEY = "order.routing.key";
    private static final String POINT_ROUTING_KEY = "point.routing.key";
    private static final String CART_ROUTING_KEY = "cart.routing.key";

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue orderQueue() {
        return new Queue(ORDER_QUEUE_NAME);
    }

    @Bean
    Queue pointQueue() {
        return new Queue(POINT_QUEUE_NAME);
    }

    @Bean
    Queue cartQueue() {
        return new Queue(CART_QUEUE_NAME);
    }

    @Bean
    Binding orderBinding(Queue orderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    Binding pointBinding(Queue pointQueue, DirectExchange exchange) {
        return BindingBuilder.bind(pointQueue).to(exchange).with(POINT_ROUTING_KEY);
    }

    @Bean
    Binding cartBinding(Queue cartQueue, DirectExchange exchange) {
        return BindingBuilder.bind(cartQueue).to(exchange).with(CART_ROUTING_KEY);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
