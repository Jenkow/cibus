package com.jll.cibus.payment.controller;

import com.jll.cibus.payment.dto.PaymentRequestDTO;
import com.jll.cibus.payment.dto.PaymentResponseDTO;
import com.jll.cibus.payment.service.PaymentService;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.dto.TableUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> findAll(){
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<PaymentResponseDTO>> findById(@PathVariable String name){
        return ResponseEntity.ok(paymentService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO response = paymentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
