package com.jll.cibus.orderdetail.controller;

import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailResponseDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailUpdateDTO;
import com.jll.cibus.orderdetail.service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<List<OrderDetailResponseDTO>> getDetailsByOrderId(@PathVariable Long orderId){
        return ResponseEntity.ok(orderDetailService.getByOrderId(orderId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<OrderDetailResponseDTO> getById(@PathVariable Long orderId, @PathVariable Long productId){
        return ResponseEntity.ok(orderDetailService.getByOrderIdAndProductId(orderId, productId));
    }

    @PostMapping
    public ResponseEntity<OrderDetailResponseDTO> create(@PathVariable Long orderId, @Valid @RequestBody OrderDetailRequestDTO detail){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailService.create(orderId, detail));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<OrderDetailResponseDTO> update(@PathVariable Long orderId, @PathVariable Long productId, @Valid @RequestBody OrderDetailUpdateDTO detail){
        return ResponseEntity.ok(orderDetailService.update(orderId, productId, detail));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> delete(@PathVariable Long orderId, @PathVariable Long productId){
        orderDetailService.delete(orderId, productId);
        return ResponseEntity.noContent().build();
    }
}
