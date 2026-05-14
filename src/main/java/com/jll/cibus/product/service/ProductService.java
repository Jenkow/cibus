package com.jll.cibus.product.service;

import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.productcategory.repository.ProductCategoryRepository;
import com.jll.cibus.product.repository.ProductRepository;
import com.jll.cibus.product.dto.ProductRequestDTO;
import com.jll.cibus.product.dto.ProductResponseDTO;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto){
        if(productRepository.findByNameIgnoreCase(dto.getName()).isPresent()) throw new ResourceAlreadyExistsException("Product", dto.getName());

        ProductEntity entity = productMapper.toEntity(dto);
        entity.setCategory(productCategoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId())));
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponseDTO(saved);
    }

    public List<ProductResponseDTO> findAll(){
        List<ProductEntity> products = productRepository.findAll();

        if(products.isEmpty()) throw new BusinessException("No products can be listed if there are no products");

        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public ProductResponseDTO findById(Long id){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Product", id));

        return productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO findByName(String name){
        ProductEntity product = productRepository.findByNameIgnoreCase(name)
                .orElseThrow( () -> new ResourceNotFoundException("Product", name));

        return productMapper.toResponseDTO(product);
    }

    public List<ProductResponseDTO> searchByName(String name){
        List<ProductEntity> products = productRepository.findAllByNameContainingIgnoreCase(name);

        if(products.isEmpty()) throw new ResourceNotFoundException("Product", name);

        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public List<ProductResponseDTO> findByCategory(Long categoryId){
        List<ProductEntity> products = productRepository.findAllByCategory_Id(categoryId);

        if(products.isEmpty()) throw new ResourceNotFoundException("Product", categoryId);

        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow( () -> new  ResourceNotFoundException("Product", id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());

        product.setCategory(productCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId())));

        ProductEntity updated = productRepository.save(product);

        return productMapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(product);
    }

}
