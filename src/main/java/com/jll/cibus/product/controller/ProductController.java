package com.jll.cibus.product.controller;

import com.jll.cibus.product.dto.ProductRequestDTO;
import com.jll.cibus.product.dto.ProductResponseDTO;
import com.jll.cibus.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api")
public class ProductController
{
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAll ()
    {
        return ResponseEntity.ok(productService.findAll());
    }
    @GetMapping ("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getById (@PathVariable Long id)
    {
        return ResponseEntity.ok(productService.findById(id));
    }
    @GetMapping ("/products/name/{name}")
    public ResponseEntity<ProductResponseDTO> getByName (@PathVariable String name)
    {
        return ResponseEntity.ok(productService.findByName(name));
    }
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponseDTO>> searchByName (@RequestParam String name)
    {
        return ResponseEntity.ok(productService.searchByName(name));
    }
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> findByCategory (@PathVariable Long categoryId)
    {
        return  ResponseEntity.ok(productService.findByCategory(categoryId));
    }
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct (@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto)
    {
        return ResponseEntity.ok(productService.update(id, dto));
    }
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> createProduct (@Valid @RequestBody ProductRequestDTO dto)
    {
        ProductResponseDTO response= productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @DeleteMapping ("/products/{id}")
    public ResponseEntity<Void> deleteProduct (@PathVariable Long id)
    {
        productService.delete(id);
        return  ResponseEntity.noContent().build();
    }
}
