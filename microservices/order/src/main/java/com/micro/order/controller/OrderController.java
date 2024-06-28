package com.micro.order.controller;

import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderRequestDto;
import com.micro.order.dto.OrderResponseDto;
import com.micro.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String placeOrder(@RequestBody OrderRequestDto orderRequest) {
       return orderService.placeOrder(orderRequest);

    }

    @GetMapping
    public List<OrderResponseDto> getOrders() {
        return orderService.getOrders();
    }
}
