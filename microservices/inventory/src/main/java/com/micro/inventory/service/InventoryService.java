package com.micro.inventory.service;

import com.micro.inventory.dto.InventoryResponse;
import com.micro.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    //without handling the exception do not use it in prod
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
//        log.info("wait Stat");
//        Thread.sleep(10000);
//        log.info("wait End");
        log.info("Checking the Stock avilability...");
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream().map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity() > 0).build()
                ).toList();
    }

    public List<InventoryResponse> findAllInventory() {
        log.info("finding all available stocks...");
        return inventoryRepository.findAll().stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0).build()).toList();
    }
}
