package com.micro.order.event;

import com.micro.order.service.OrderService;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Setter
@Getter
public class OrderPlacedEvent extends ApplicationEvent {
    private String orderNumber;


    public OrderPlacedEvent(Object source, String orderNumber) {
        super(source);
        this.orderNumber = orderNumber;
    }

    public OrderPlacedEvent(String orderNumber) {
        super(orderNumber);
        this.orderNumber = orderNumber;
    }
}
