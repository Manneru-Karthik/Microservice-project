package com.example.coding.order_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.coding.order_service.dto.InventoryResponse;
import com.example.coding.order_service.dto.OrderLineItemsDto;
import com.example.coding.order_service.dto.OrderRequest;
import com.example.coding.order_service.model.Order;
import com.example.coding.order_service.model.OrderLineItems;
import com.example.coding.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    
    public void placeOrder(OrderRequest orderRequest){
            Order order=new Order();
            order.setOrderNumber(UUID.randomUUID().toString());

           List<OrderLineItems> orderLineItems= orderRequest.getOrderLineItemsDtoList().stream()
            .map(this::mapToDto)
            .toList();

            order.setOrderLineItemsList(orderLineItems);

            List<String> skuCodes=order.getOrderLineItemsList().stream()
            .map(OrderLineItems::getSkuCode)
            .toList();
            //save the order only if the product is available in stock
           InventoryResponse[] inventoryResponseArray= webClientBuilder.build().get().uri("http://inventory-service/api/inventory",
           uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
            .retrieve()
            .bodyToMono(InventoryResponse[].class)
            .block();
      
            boolean allProductsInStock=Arrays.stream(inventoryResponseArray)
                          .allMatch(InventoryResponse::isInStock);

            if(allProductsInStock){
            orderRepository.save(order);
            }else{
                throw new IllegalArgumentException("Product is not in stock,please try again later");
            }
        }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
