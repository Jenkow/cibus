package com.jll.cibus.branchproduct.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.branchproduct.dto.BranchProductRequestDTO;
import com.jll.cibus.branchproduct.dto.BranchProductResponseDTO;
import com.jll.cibus.branchproduct.dto.BranchProductUpdateDTO;
import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.branchproduct.mapper.BranchProductMapper;
import com.jll.cibus.branchproduct.repository.BranchProductRepository;
import com.jll.cibus.branchproduct.specification.BranchProductSpecification;
import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BranchProductService {

    private final BranchProductRepository branchProductRepository;
    private final BranchProductMapper branchProductMapper;
    private final BranchService branchService;
    private final ProductService productService;


    public Page<BranchProductResponseDTO> findAll(Pageable pageable, Long branchId, Long productId, String productName, Long categoryId, Boolean available, BigDecimal minPrice, BigDecimal maxPrice){
        Specification<BranchProductEntity> spec = Specification.allOf(
                BranchProductSpecification.equalsBranchId(branchId),
                BranchProductSpecification.equalsProductId(productId),
                BranchProductSpecification.containsProductName(productName),
                BranchProductSpecification.equalsCategoryId(categoryId),
                BranchProductSpecification.isAvailable(available),
                BranchProductSpecification.priceGreaterThanOrEqualTo(minPrice),
                BranchProductSpecification.priceLessThanOrEqualTo(maxPrice)
        );

        return branchProductRepository.findAll(spec, pageable)
                .map(branchProductMapper::toDTO);
    }

    public BranchProductEntity getEntity(Long id){
        return branchProductRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Menu Item", id));
    }

    public BranchProductEntity getEntityByBranchAndProduct(Long branchId, Long productId){
        branchService.getEntity(branchId);
        productService.getEntity(productId);
        return branchProductRepository.findByBranch_IdAndProduct_Id(branchId, productId).
                orElseThrow(() -> new ResourceNotFoundException("Menu Item", productId));
    }

    public BranchProductResponseDTO getByBranchAndProduct(Long branchId, Long productId){
        BranchProductEntity product = getEntityByBranchAndProduct(branchId, productId);
        return branchProductMapper.toDTO(product);
    }

    public BranchProductResponseDTO create (Long branchId, BranchProductRequestDTO dto){
        BranchEntity branch = branchService.getEntity(branchId);
        ProductEntity product = productService.getEntity(dto.getProductId());
        if(branchProductRepository.existsByBranch_IdAndProduct_Id(branchId, dto.getProductId())){
            throw new ResourceAlreadyExistsException("El producto con id"+dto.getProductId()+" ya existe en la sucursal con id"+branchId);
        }
        BranchProductEntity entity = branchProductMapper.toEntity(dto);
        entity.setBranch(branch);
        entity.setProduct(product);
        entity.setAvailable(Boolean.TRUE);
        BranchProductEntity saved = branchProductRepository.save(entity);
        return branchProductMapper.toDTO(saved);
    }

    public BranchProductResponseDTO update(Long branchId, Long productId, BranchProductUpdateDTO dto){
        BranchProductEntity entity = getEntityByBranchAndProduct(branchId, productId);
        if(dto.getPrice() != null){
            entity.setPrice(dto.getPrice());
        }
        if(dto.getAvailable() != null){
            entity.setAvailable(dto.getAvailable());
        }
        BranchProductEntity saved = branchProductRepository.save(entity);
        return branchProductMapper.toDTO(saved);
    }

    private void changeAvailability(Long id, Boolean available){
        BranchProductEntity entity = getEntity(id);
        entity.setAvailable(available);
        branchProductRepository.save(entity);
    }

    public void enable(Long id){
        changeAvailability(id, Boolean.TRUE);
    }

    public void disable(Long id){
        changeAvailability(id, Boolean.FALSE);
    }

    public void delete(Long branchId, Long productId){
        BranchProductEntity entity = getEntityByBranchAndProduct(branchId, productId);
        branchProductRepository.delete(entity);
    }

}