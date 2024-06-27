package com.micro.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.product.dto.ProductRequest;
import com.micro.product.dto.ProductResponse;
import com.micro.product.utility.MapToProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@RequiredArgsConstructor
class ProductApplicationTests {

    //Mongo db docker Container
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    //end point like POSTMAN
    @Autowired
    private MockMvc mockMvc;
    //use to POJO to String
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MapToProduct mapToProduct;

    //we use docker img that why we are get the DB URL & configure it dynamically
    @DynamicPropertySource
    static void setPropertys(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mangodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void createProduct() throws Exception {
        ProductRequest productRequet = getProductRequest();
        //convert to String
        String requestString = objectMapper.writeValueAsString(productRequet);
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isCreated());

    }

    @Test
    void getProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    void getProduct(MvcResult mvcPostResultid) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/product/" + mvcPostResultid.getResponse().getContentAsString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(a -> Assertions.assertEquals(mvcPostResultid.getResponse().getContentAsString(),
                        objectMapper.readValue(a.getResponse().getContentAsString(), ProductResponse.class).getId()));
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder().name("LED TV").description("Electronic").price(BigDecimal.valueOf(5000)).build();
    }

}
