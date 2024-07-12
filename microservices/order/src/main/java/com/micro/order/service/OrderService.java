package com.micro.order.service;

import com.micro.order.constant.AppConstant;
import com.micro.order.dto.InventoryResponse;
import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.event.OrderPlacedEvent;
import com.micro.order.model.Order;
import com.micro.order.model.OrderLineItems;
import com.micro.order.repository.OrderRepository;
import com.micro.order.utility.MapToDto;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final ObservationRegistry observationRegistry;
    private final KafkaTemplate<String, Object> stringObjectKafkaTemplate;

    public String placeOrder(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        //multiple product order request
        List<OrderLineItems> orderLineItems = orderRequestDto.getOrderLineItemsDtoList()
                .stream()
                .map(mapToDto::mapOrderLineItemsDtoToOrderLineItems)
                .toList();
        order.setOrderLineItems(orderLineItems);
        //multiple sku was handel
        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode).toList();

        // Call Inventory Service, and place order if product is in stock
        Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
                this.observationRegistry);
        log.info("Calling the INVENTORY-SERVICE");
        inventoryServiceObservation.lowCardinalityKeyValue("call", "INVENTORY-SERVICE");
        //inventoryServiceObservation is used to maintain the same SPAN ID to tract it's single request
        return inventoryServiceObservation.observe(() -> {
            InventoryResponse[] inventoryResponse = webClientBuilder.build().get().uri("http://INVENTORY-SERVICE/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve().bodyToMono(InventoryResponse[].class)
                    .block();
            assert inventoryResponse != null;
            log.info("inventoryResponse :: {}", Arrays.stream(inventoryResponse).toList());
            if (inventoryResponse.length > 0) {
                //check all product is in stock then only placed the order
                boolean allProductInStock = Arrays.stream(inventoryResponse).allMatch(InventoryResponse::isInStock);
                if (allProductInStock) {
                    orderRepository.save(order);
                    try {
                        stringObjectKafkaTemplate.send(AppConstant.ORDER_NOTIFICATION_TOPIC, new OrderPlacedEvent(order.getOrderNumber()));
                    }catch (Exception e)
                    {
                        log.error(e.getMessage());
                    }
                    log.info("Order placed & notification was send...");
                    return "Order Placed! Order Number is :: " + order.getOrderNumber();
                } else {
                    log.info("Product is out of stock...");
                    return "Sorry.. Product is not in stock, please try again latter.";
                }
            } else {
                log.info("product is not in list...");
                return "Product is not Valid, please try again latter.";
            }
        });
    }

    public List<OrderResponseDto> getOrders() {
        log.info("fetching all placed Orders...");
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(mapToDto::mapOrderToOrderResponseDto).toList();
    }


}
