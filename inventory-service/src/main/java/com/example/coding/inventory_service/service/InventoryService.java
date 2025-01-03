package com.example.coding.inventory_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.coding.inventory_service.dto.InventoryResponse;
import com.example.coding.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
//import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class InventoryService {


    private final InventoryRepository inventoryRepository;
    
    //@Transactional(readOnly=true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
          return inventoryRepository.findBySkuCodeIn(skuCode)
          .stream()
          .map(inventory -> 
            InventoryResponse.builder()
            .skuCode(inventory.getSkuCode())
            .isInStock(inventory.getQuantity()>0)
            .build()
          )
          .toList();
    }
}
