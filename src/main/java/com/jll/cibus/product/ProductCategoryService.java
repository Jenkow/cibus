package com.jll.cibus.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    @Transactional
    public ProductCategoryResponseDTO create (ProductCategoryRequestDTO dto){
        if(productCategoryRepository.findByNameIgnoreCase(dto.getName()).isPresent()){
            throw new RuntimeException("Product category already exists");
        }
        ProductCategoryEntity entity = productCategoryMapper.toEntity(dto);
        ProductCategoryEntity saved = productCategoryRepository.save(entity);
        return productCategoryMapper.toDTO(saved);
    }

    public List<ProductCategoryResponseDTO> findAll (){
        List<ProductCategoryEntity> categories = productCategoryRepository.findAll();
        return categories.stream()
                .map(productCategoryMapper::toDTO)
                .toList();
    }

    public ProductCategoryResponseDTO findById(Long id){
        ProductCategoryEntity category = productCategoryRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Product category not found"));//Despues vemos excepciones personalizadas
        return productCategoryMapper.toDTO(category);
    }

    public ProductCategoryResponseDTO findByName(String name){
        ProductCategoryEntity category = productCategoryRepository.findByNameIgnoreCase(name)
                .orElseThrow( () -> new RuntimeException("Product category not found"));
        return productCategoryMapper.toDTO(category);
    }

    @Transactional
    public ProductCategoryResponseDTO update(Long id, ProductCategoryRequestDTO dto){
        ProductCategoryEntity category = productCategoryRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Product category not found"));
        Optional<ProductCategoryEntity> existing = productCategoryRepository.findByNameIgnoreCase(dto.getName());
        if(existing.isPresent() && !existing.get().getId().equals(id)){
            throw new RuntimeException("Product category already exists");
        }
        category.setName(dto.getName());
        ProductCategoryEntity saved = productCategoryRepository.save(category);
        return productCategoryMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long id){
        ProductCategoryEntity category = productCategoryRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Product category not found"));
        productCategoryRepository.delete(category);
    }
}
