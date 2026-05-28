package com.jll.cibus.branchproduct.controller;


import com.jll.cibus.branch.dto.BranchResponseDTO;
import com.jll.cibus.branchproduct.dto.BranchProductRequestDTO;
import com.jll.cibus.branchproduct.dto.BranchProductResponseDTO;
import com.jll.cibus.branchproduct.dto.BranchProductUpdateDTO;
import com.jll.cibus.branchproduct.service.BranchProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class BranchProductController {

    private final BranchProductService branchProductService;

    public BranchProductController(BranchProductService branchProductService){
        this.branchProductService = branchProductService;
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<BranchProductResponseDTO>> getMenuByBranchId(
            @PathVariable Long branchId,
            @RequestParam(required = false) Boolean available){
        if(Boolean.TRUE.equals(available)){
            return ResponseEntity.ok(branchProductService.findAvailableByBranchId(branchId));
        }
        return ResponseEntity.ok(branchProductService.getByBranchId(branchId));
    }

    @GetMapping("/branch/name/{branchName}")
    public ResponseEntity<List<BranchProductResponseDTO>> getMenuByBranchName(
            @PathVariable String branchName,
            @RequestParam(required = false) Boolean available) {
        if (Boolean.TRUE.equals(available)) {
            return ResponseEntity.ok(branchProductService.findAvailableByBranchName(branchName));
        }
        return ResponseEntity.ok(branchProductService.getByBranchName(branchName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchProductResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(branchProductService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<BranchProductResponseDTO> getByBranchAndProductId(@RequestParam Long branchId, @RequestParam Long productId){
        return ResponseEntity.ok(branchProductService.getByBranchAndProduct(branchId, productId));
    }

    @PostMapping
    public ResponseEntity<BranchProductResponseDTO> create(@Valid @RequestBody BranchProductRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(branchProductService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BranchProductUpdateDTO dto){
        return ResponseEntity.ok(branchProductService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        branchProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
