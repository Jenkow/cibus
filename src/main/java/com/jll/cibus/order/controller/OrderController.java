package com.jll.cibus.order.controller;

import com.jll.cibus.order.service.OrderService;
import com.jll.cibus.order.dto.OrderRequestDTO;
import com.jll.cibus.order.dto.OrderResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/{branchId}/orders")
public class OrderController
{
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<List<OrderResponseDTO>> getByBranchId (@PathVariable Long branchId)
    {
        return ResponseEntity.ok(orderService.findByBranchId(branchId));
    }

    @GetMapping ("/table/{tableId}")
    public ResponseEntity<List<OrderResponseDTO>> getByTableId (@PathVariable Long tableId)
    {
        return ResponseEntity.ok(orderService.findByTableId(tableId));
    }
    @GetMapping ("/waiter/{waiterId}")
    public  ResponseEntity<List<OrderResponseDTO>> getByWaiterId (@PathVariable Long waiterId)
    {
        return ResponseEntity.ok(orderService.findByWaiterId(waiterId));
    }
    @GetMapping ("/status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getByBranchAndStatus (@PathVariable Long branchId, @PathVariable String status)
    {
        return ResponseEntity.ok(orderService.findByBranchAndStatus(branchId, status));
    }
    @GetMapping ("/paid/{paid}")
    public ResponseEntity<List<OrderResponseDTO>> getByPaid (@PathVariable Boolean paid)
    {
        return ResponseEntity.ok(orderService.findByPaid(paid));
    }
    @PostMapping()
    public ResponseEntity<OrderResponseDTO> createOrder (@Valid @RequestBody OrderRequestDTO dto)
    {
        OrderResponseDTO response = orderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping ("/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder (@PathVariable Long orderId, @Valid @RequestBody OrderRequestDTO dto)
    {
        return ResponseEntity.ok(orderService.update(dto));
    }

    //PROBABLEMENTE SEA MEJOR EL CAMBIAR A ESTADO CANCELADO ASI QUEDA GUARDADO
    @DeleteMapping ("/{orderId}")
    public ResponseEntity<Void> deleteOrder (@PathVariable Long orderId)
    {
        orderService.delete(orderId);
        return  ResponseEntity.noContent().build();
    }

}
