package com.micro.order.service;

import com.micro.order.dto.InventoryResponse;
import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.model.Order;
import com.micro.order.model.OrderLineItems;
import com.micro.order.repository.OrderRepository;
import com.micro.order.utility.MapToDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MapToDto mapToDto;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequestDto.getOrderLineItemsDtoList()
                .stream()
                .map(mapToDto::mapOrderLineItemsDtoToOrderLineItems)
                .toList();
        order.setOrderLineItems(orderLineItems);

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponse = webClientBuilder.build().get().uri("http://INVENTORY-SERVICE/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve().bodyToMono(InventoryResponse[].class)
                .block();
        assert inventoryResponse != null;
        log.info("inventoryResponse :: {}", Arrays.stream(inventoryResponse).toList());
        if (inventoryResponse.length > 0) {
            boolean allProductInStock = Arrays.stream(inventoryResponse).allMatch(InventoryResponse::isInStock);
            if (allProductInStock) {
                return "Order Placed! Order Number is :: " + orderRepository.save(order).getOrderNumber();
            } else {
                return "Product is not in stock, please try again latter.";
            }
        } else {
            return "Product is not Valid, please try again latter.";
        }
    }

    public List<OrderResponseDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(mapToDto::mapOrderToOrderResponseDto).toList();
    }


}
