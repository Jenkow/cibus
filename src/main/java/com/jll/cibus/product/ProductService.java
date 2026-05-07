package com.jll.cibus.product;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO create(ProductRequestDTO dto){
        if(productRepository.findByNameIgnoreCase(dto.getName()).isPresent()){
            throw new RuntimeException("Product already exists");
        }
        ProductEntity entity = productMapper.toEntity(dto);
        entity.setCategory(productCategoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found")));
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
                .orElseThrow( () -> new RuntimeException("Product not found"));//Despues vemos excepciones personalizadas
        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO findByName(String name){
        ProductEntity product = productRepository.findByNameIgnoreCase(name)
                .orElseThrow( () -> new RuntimeException("Product not found"));
        return productMapper.toResponseDTO(product);
    }

    public List<ProductResponseDTO> searchByName(String name){
        List<ProductEntity> products = productRepository.findAllByNameContainingIgnoreCase(name);
        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public List<ProductResponseDTO> findByCategory(Long categoryId){
        List<ProductEntity> products = productRepository.findAllByCategory_Id(categoryId);
        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));
        ProductEntity updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

}
