package com.micro.product.service;

import com.micro.product.dto.ProductRequest;
import com.micro.product.dto.ProductResponse;
import com.micro.product.model.Product;
import com.micro.product.repository.ProductRepository;
import com.micro.product.utility.MapToProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {


    private final ProductRepository productRepository;
    private final MapToProduct mapToProduct;

    public String createProduact(ProductRequest produacRequest) {
        Product product = Product.builder()
                .name(produacRequest.getName())
                .description(produacRequest.getDescription())
                .price(produacRequest.getPrice()).build();
        productRepository.save(product);
        log.info("Product {} is save", product.getId());
        return product.getId();

    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(mapToProduct::mapToProductResponse).toList();
    }

    public ProductResponse getProductsById(String id) {
        Optional<Product> products = productRepository.findById(id);
        return products.stream().map(mapToProduct::mapToProductResponse).toList().get(0);
    }


}
