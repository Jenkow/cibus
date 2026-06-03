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
@RequestMapping("/api/branches/{branchId}/menu")
public class BranchProductController {

    private final BranchProductService branchProductService;

    @GetMapping
    public ResponseEntity<List<BranchProductResponseDTO>> getMenuByBranchId(
            @PathVariable Long branchId,
            @RequestParam(required = false) Boolean available){
        if(Boolean.TRUE.equals(available)){
            return ResponseEntity.ok(branchProductService.findAvailableByBranchId(branchId));
        }
        return ResponseEntity.ok(branchProductService.getByBranchId(branchId));
    }

    @GetMapping("name/{branchName}")
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
    public ResponseEntity<List<BranchProductResponseDTO>> search(@RequestParam Long branchId, // El required es true ya que este metodo no esta pensado para ser utilziado por nadie que no tenga acceso a todas las branches, es decir, un empleado
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) Long categoryId,
                                                                 @RequestParam(required = false) Boolean available,
                                                                 @RequestParam(required = false) BigDecimal price,
                                                                 @RequestParam(required = false) BigDecimal minPrice,
                                                                 @RequestParam(required = false) BigDecimal maxPrice){

        return ResponseEntity.ok(branchProductService.search(branchId, name, categoryId, available, price, minPrice, maxPrice));
    }

    @PostMapping
    public ResponseEntity<BranchProductResponseDTO> create(@PathVariable Long branchId, @Valid @RequestBody BranchProductRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(branchProductService.create(branchId, dto));
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
