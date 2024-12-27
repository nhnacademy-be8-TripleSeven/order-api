package com.tripleseven.orderapi.common.rabbit;

import com.tripleseven.orderapi.dto.CombinedMessageDTO;
import com.tripleseven.orderapi.dto.cartitem.CartItemDTO;
import com.tripleseven.orderapi.dto.cartitem.WrappingCartItemDTO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    private static final String EXCHANGE_NAME = "nhn24.pay.exchange";
    private static final String DEAD_EXCHANGE_NAME = "nhn24.pay.dlx";

    private static final String ORDER_QUEUE_NAME = "nhn24.order.queue";
    private static final String POINT_QUEUE_NAME = "nhn24.point.queue";
    private static final String CART_QUEUE_NAME = "nhn24.cart.queue";

    private static final String ORDER_ROUTING_KEY = "order.routing.key";
    private static final String POINT_ROUTING_KEY = "point.routing.key";
    private static final String CART_ROUTING_KEY = "cart.routing.key";

    private static final String ORDER_DEAD_ROUTING_KEY = "order.dlk.key";
    private static final String POINT_DEAD_ROUTING_KEY = "point.dlk.key";
    private static final String CART_DEAD_ROUTING_KEY = "cart.dlk.key";


    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE_NAME)
                .deadLetterExchange(DEAD_EXCHANGE_NAME)
                .deadLetterRoutingKey(ORDER_DEAD_ROUTING_KEY)
                .build();
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
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.tripleseven.orderapi.dto.CombinedMessageDTO", CombinedMessageDTO.class);

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);

        return rabbitTemplate;
    }
}
