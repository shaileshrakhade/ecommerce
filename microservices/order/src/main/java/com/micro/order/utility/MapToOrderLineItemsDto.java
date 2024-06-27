package com.micro.order.utility;

import com.micro.order.dto.OrderLineItemsDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.model.Order;
import com.micro.order.model.OrderLineItems;
import org.springframework.stereotype.Component;

@Component
public class MapToOrderLineItemsDto implements MapToDto {


    @Override
    public OrderLineItems mapOrderLineItemsDtoToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    @Override
    public OrderLineItemsDto mapOrderLineItemsToOrderLineItemsDto(OrderLineItems orderLineItems) {
        OrderLineItemsDto lineItemsDto = new OrderLineItemsDto();
        lineItemsDto.setId(orderLineItems.getId());
        lineItemsDto.setQuantity(orderLineItems.getQuantity());
        lineItemsDto.setPrice(orderLineItems.getPrice());
        lineItemsDto.setSkuCode(orderLineItems.getSkuCode());
        return lineItemsDto;
    }

    @Override
    public OrderResponseDto mapOrderToOrderResponseDto(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(order.getId());
        orderResponseDto.setOrderNumber(order.getOrderNumber());
        orderResponseDto.setOrderLineItemsDtoList(order.getOrderLineItems()
                .stream().map(this::mapOrderLineItemsToOrderLineItemsDto).toList());
        return orderResponseDto;
    }
}

