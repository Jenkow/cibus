package com.jll.cibus.orderdetail.controller;

import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailResponseDTO;
import com.jll.cibus.orderdetail.service.OrderDetailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService){
        this.orderDetailService = orderDetailService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDetailResponseDTO>> getDetailsByOrderId(@PathVariable Long orderId){
        return ResponseEntity.ok(orderDetailService.getByOrderId(orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponseDTO> getById(@PathVariable Long orderId, @PathVariable Long detailId){
        return ResponseEntity.ok(orderDetailService.getById(orderId, detailId));
    }

    @PostMapping
    public ResponseEntity<OrderDetailResponseDTO> create(@PathVariable Long orderId, @Valid @RequestBody OrderDetailRequestDTO detail){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDetailService.create(orderId, detail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponseDTO> update(@PathVariable Long orderId, @PathVariable Long detailId, @Valid @RequestBody OrderDetailRequestDTO detail){
        return ResponseEntity.ok(orderDetailService.update(orderId, detailId, detail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long orderId, @PathVariable Long detailId){
        orderDetailService.delete(orderId, detailId);
        return ResponseEntity.noContent().build();
    }
}
