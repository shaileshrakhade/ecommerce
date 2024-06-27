package com.micro.order.service;

import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.model.Order;
import com.micro.order.model.OrderLineItems;
import com.micro.order.repository.OrderRepository;
import com.micro.order.utility.MapToDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MapToDto mapToDto;

    public void placeOrder(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequestDto.getOrderLineItemsDtoList()
                .stream()
                .map(mapToDto::mapOrderLineItemsDtoToOrderLineItems)
                .toList();
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);
    }

    public List<OrderResponseDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(mapToDto::mapOrderToOrderResponseDto).toList();
    }


}
