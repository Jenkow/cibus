package com.jll.cibus.branchproduct.controller;

import com.jll.cibus.branchproduct.dto.BranchProductRequestDTO;
import com.jll.cibus.branchproduct.dto.BranchProductResponseDTO;
import com.jll.cibus.branchproduct.dto.BranchProductUpdateDTO;
import com.jll.cibus.branchproduct.service.BranchProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/branches/{branchId}/products")
public class BranchProductController {

    private final BranchProductService branchProductService;

    @GetMapping
    public ResponseEntity<List<BranchProductResponseDTO>> search(@PathVariable Long branchId,
                                                                 @RequestParam(required = false) Long productId,
                                                                 @RequestParam(required = false) String productName,
                                                                 @RequestParam(required = false) Long categoryId,
                                                                 @RequestParam(required = false) Boolean available,
                                                                 @RequestParam(required = false) BigDecimal minPrice,
                                                                 @RequestParam(required = false) BigDecimal maxPrice){
        return ResponseEntity.ok(branchProductService.search(branchId, productId, productName, categoryId, available, minPrice, maxPrice));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<BranchProductResponseDTO> getByBranchIdAndProductId(@PathVariable Long branchId, @PathVariable Long productId){
        return ResponseEntity.ok(branchProductService.getByBranchAndProduct(branchId, productId));
    }

    @PostMapping
    public ResponseEntity<BranchProductResponseDTO> create(@PathVariable Long branchId, @Valid @RequestBody BranchProductRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(branchProductService.create(branchId, dto));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<BranchProductResponseDTO> update(@PathVariable Long branchId, @PathVariable Long productId, @Valid @RequestBody BranchProductUpdateDTO dto){
        return ResponseEntity.ok(branchProductService.update(branchId, productId, dto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> delete(@PathVariable Long branchId, @PathVariable Long productId){
        branchProductService.delete(branchId, productId);
        return ResponseEntity.noContent().build();
    }

    /* ------------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<BranchProductResponseDTO> getById(@PathVariable Long id){                       por como es el endpoint me parece mucho mas legible que se busque por
        return ResponseEntity.ok(branchProductService.getById(id));                                       el branchId y productId antes que el id de la relacion.
    }

    @GetMapping("name/{branchName}")
    public ResponseEntity<List<BranchProductResponseDTO>> getMenuByBranchName(                             La dejo comentada porque el branchId ya llega siempre por el endpoint,
            @PathVariable String branchName,                                                               entonces la vamos a querer buscar por el nombre en algun momento?
            @RequestParam(required = false) Boolean available) {
        if (Boolean.TRUE.equals(available)) {
            return ResponseEntity.ok(branchProductService.findAvailableByBranchName(branchName));
        }
        return ResponseEntity.ok(branchProductService.getByBranchName(branchName));
    }
     */

}
