package com.micro.order.dto;

import com.micro.order.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    List<OrderLineItemsDto> orderLineItemsDtoList;
}
