package com.micro.order.controller;

import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    inventory is the id of Circuit Breaker it will track configure from proprieties file
//    @CircuitBreaker is use when the other microservice is down then it will hande it & call the fallback method
//    if the service is not available then move it to Open
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackAfterRetryCircuitBreaker")
//    waiting for response till the time mention in properties file here we have fallback method a well if we want t handle it
    @TimeLimiter(name = "inventory", fallbackMethod = "fallbackAfterTimeLimiter")
//    retry when request is fail maximum attempt mention in properties file here also we have fallback method
    @Retry(name = "inventory", fallbackMethod = "fallbackAfterRetry")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequestDto orderRequest) {
        log.info("Placing Order");
        return CompletableFuture.supplyAsync(() ->
                orderService.placeOrder(orderRequest)
        );

    }

    @GetMapping
    public List<OrderResponseDto> getOrders() {
        return orderService.getOrders();
    }

    //fallback method call in CircuitBreaker it need to be same rerun type & signature
    public CompletableFuture<String> fallbackAfterRetryCircuitBreaker(OrderRequestDto orderRequestDto, RuntimeException runtimeException) {
        log.error("Cannot Place Order Executing circuitBreakerFallbackMethod logic");
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time! :: circuitBreakerFallbackMethod");
    }

    public CompletableFuture<String> fallbackAfterTimeLimiter(OrderRequestDto orderRequestDto, RuntimeException runtimeException) {
        log.error("Cannot Place Order Executing timeLimiterFallbackMethod logic");
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time! :: timeLimiterFallbackMethod");
    }

    public CompletableFuture<String> fallbackAfterRetry(OrderRequestDto orderRequestDto, RuntimeException runtimeException) {
        log.error("Cannot Place Order Executing fallbackAfterRetry  logic");
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time! :: fallbackAfterRetry");
    }
}
