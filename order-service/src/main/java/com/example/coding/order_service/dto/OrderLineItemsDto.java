package com.example.coding.order_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItemsDto {
    
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
