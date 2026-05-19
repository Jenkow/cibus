package com.jll.cibus.branchproduct.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.branchproduct.dto.BranchProductRequestDTO;
import com.jll.cibus.branchproduct.dto.BranchProductResponseDTO;
import com.jll.cibus.branchproduct.dto.BranchProductUpdateDTO;
import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.branchproduct.mapper.BranchProductMapper;
import com.jll.cibus.branchproduct.repository.BranchProductRepository;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.repository.ProductRepository;
import com.jll.cibus.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BranchProductService {

    private final BranchProductRepository branchProductRepository;
    private final BranchProductMapper branchProductMapper;
    private final BranchService branchService;
    private final ProductService productService;

    public BranchProductService(BranchProductRepository branchProductRepository, BranchProductMapper branchProductMapper, BranchService branchService, ProductService productService) {
        this.branchProductRepository = branchProductRepository;
        this.branchProductMapper = branchProductMapper;
        this.branchService = branchService;
        this.productService = productService;
    }

    public List<BranchProductResponseDTO> getByBranchId(Long branchId){
        if(!branchService.existsById(branchId)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_Id(branchId);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public List<BranchProductResponseDTO> getByBranchName(String branchName){
        if(!branchService.existsByName(branchName)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_Name(branchName);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public List<BranchProductResponseDTO> findAvailableByBranchId(Long branchId){
        if(!branchService.existsById(branchId)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_IdAndAvailableTrue(branchId);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public List<BranchProductResponseDTO> findAvailableByBranchName(String branchName){
        if(!branchService.existsByName(branchName)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_NameAndAvailableTrue(branchName);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public BranchProductResponseDTO getById(Long id){
        BranchProductEntity product = branchProductRepository.findById(id).
                orElseThrow(() -> new RuntimeException("No se marco el producto en el menu de la sucursal"));
        return branchProductMapper.toDTO(product);
    }

    public BranchProductEntity getEntity(Long id){
        return branchProductRepository.findById(id).
                orElseThrow(() -> new RuntimeException("No se marco el producto en el menu de la sucursal"));
    }

    public BranchProductEntity getEntityByBranchAndProduct(Long branchId, Long productId){
        branchService.getEntity(branchId);
        productService.getEntity(productId);
        return branchProductRepository.findByBranch_IdAndProduct_Id(branchId, productId).
                orElseThrow(() -> new RuntimeException("No se marco el producto en el menu de la sucursal"));
    }

    public BranchProductResponseDTO getByBranchAndProduct(Long branchId, Long productId){
        branchService.getEntity(branchId);
        productService.getEntity(productId);
        BranchProductEntity entity = getEntityByBranchAndProduct(branchId, productId);
        return branchProductMapper.toDTO(entity);
    }

    public BranchProductResponseDTO create (BranchProductRequestDTO dto){
        BranchEntity branch = branchService.getEntity(dto.getBranchId());
        ProductEntity product = productService.getEntity(dto.getProductId());
        if(branchProductRepository.existsByBranch_IdAndProduct_Id(dto.getBranchId(), dto.getProductId())){
            throw new RuntimeException("El producto ya existe en la sucursal");
        }
        BranchProductEntity entity = branchProductMapper.toEntity(dto);
        entity.setBranch(branch);
        entity.setProduct(product);
        BranchProductEntity saved = branchProductRepository.save(entity);
        return branchProductMapper.toDTO(saved);
    }

    public BranchProductResponseDTO update(Long id, BranchProductUpdateDTO dto){
        BranchProductEntity entity = getEntity(id);
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

    public void delete(Long id){
        BranchProductEntity entity = getEntity(id);
        branchProductRepository.delete(entity);
    }
}
