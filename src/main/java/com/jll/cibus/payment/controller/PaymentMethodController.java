package com.jll.cibus.payment.controller;

import com.jll.cibus.payment.dto.PaymentMethodRequestDTO;
import com.jll.cibus.payment.dto.PaymentMethodResponseDTO;
import com.jll.cibus.payment.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponseDTO>> findAll(){
        return ResponseEntity.ok(paymentMethodService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(paymentMethodService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<PaymentMethodResponseDTO>> findByName(@PathVariable String name){
        return ResponseEntity.ok(paymentMethodService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<PaymentMethodResponseDTO> create(@Valid @RequestBody PaymentMethodRequestDTO dto) {
        PaymentMethodResponseDTO response = paymentMethodService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PaymentMethodRequestDTO dto) {
        return ResponseEntity.ok(paymentMethodService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
