package com.jll.cibus.product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    //private final ProductCategoryService productCategoryService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO create(ProductRequestDTO dto){
        ProductEntity entity = productMapper.toEntity(dto);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponseDTO(saved);
    }

    public List<ProductResponseDTO> findAll(){
        List<ProductEntity> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public ProductResponseDTO findById(Long id){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Product not found"));       //Despues vemos excepciones personalizadas
        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        //product.setCategory(productCategoryService.findById(dto.getCategoryId()));
        ProductEntity updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

}
