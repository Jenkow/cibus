package com.jll.cibus.order.controller;

import com.jll.cibus.order.dto.OrderUpdateDTO;
import com.jll.cibus.order.service.OrderService;
import com.jll.cibus.order.dto.OrderRequestDTO;
import com.jll.cibus.order.dto.OrderResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/branches/{branchId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll(@PathVariable Long branchId,
                                                         @RequestParam(required = false) Long tableNumber,
                                                         @RequestParam(required = false) Long waiterId,
                                                         @RequestParam(required = false) String statusName,
                                                         @RequestParam(required = false) LocalDateTime from,
                                                         @RequestParam(required = false) LocalDateTime to,
                                                         @RequestParam(required = false) BigDecimal minTotal,
                                                         @RequestParam(required = false) BigDecimal maxTotal){
        return ResponseEntity.ok(orderService.getAll(branchId, tableNumber, waiterId, statusName, from, to, minTotal, maxTotal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<OrderResponseDTO> create(@PathVariable Long branchId, @Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO response = orderService.create(branchId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> update(@PathVariable Long branchId, @PathVariable Long orderId, @Valid @RequestBody OrderUpdateDTO dto) {
        return ResponseEntity.ok(orderService.update(branchId, orderId, dto));
    }

    //PROBABLEMENTE SEA MEJOR EL CAMBIAR A ESTADO CANCELADO ASI QUEDA GUARDADO
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> delete(@PathVariable Long orderId) {
        orderService.delete(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getStatuses(){
        return ResponseEntity.ok(orderService.getStatuses());
    }

}
