package com.jll.cibus.product.service;

import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.product.repository.ProductRepository;
import com.jll.cibus.product.dto.ProductRequestDTO;
import com.jll.cibus.product.dto.ProductResponseDTO;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.mapper.ProductMapper;
import com.jll.cibus.productcategory.service.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService productCategoryService;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductCategoryService productCategoryService, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.productCategoryService = productCategoryService;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto){
        if(productRepository.findByNameIgnoreCase(dto.getName()).isPresent()) throw new ResourceAlreadyExistsException("Product", dto.getName());
        ProductEntity entity = productMapper.toEntity(dto);
        entity.setCategory(productCategoryService.getEntity(dto.getCategoryId()));
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponseDTO(saved);
    }

    public List<ProductResponseDTO> findAll(){
        List<ProductEntity> products = productRepository.findAll();

        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    public Boolean existsById(Long id){
        return productRepository.existsById(id);
    }

    public ProductEntity getEntity (Long id){
        return productRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Product", id));
    }

    public ProductResponseDTO findById(Long id){
        ProductEntity product = getEntity(id);
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

        return products.stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto){
        ProductEntity product = getEntity(id);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(productCategoryService.getEntity(dto.getCategoryId()));
        ProductEntity updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        ProductEntity product = getEntity(id);
        productRepository.delete(product);
    }

}
