package com.micro.order.utility;

import com.micro.order.dto.OrderLineItemsDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.model.Order;
import com.micro.order.model.OrderLineItems;
import org.springframework.stereotype.Component;

@Component
public interface MapToDto {
    OrderLineItems mapOrderLineItemsDtoToOrderLineItems(OrderLineItemsDto orderLineItemsDto);

    OrderLineItemsDto mapOrderLineItemsToOrderLineItemsDto(OrderLineItems orderLineItems);

    OrderResponseDto mapOrderToOrderResponseDto(Order order);

}
