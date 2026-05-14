package com.jll.cibus.branchproduct.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.branchproduct.dto.BranchProductRequestDTO;
import com.jll.cibus.branchproduct.dto.BranchProductResponseDTO;
import com.jll.cibus.branchproduct.dto.BranchProductUpdateDTO;
import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.branchproduct.mapper.BranchProductMapper;
import com.jll.cibus.branchproduct.repository.BranchProductRepository;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BranchProductService {

    private final BranchProductRepository branchProductRepository;
    private final BranchProductMapper branchProductMapper;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public BranchProductService(BranchProductRepository branchProductRepository, BranchProductMapper branchProductMapper, BranchRepository branchRepository, ProductRepository productRepository) {
        this.branchProductRepository = branchProductRepository;
        this.branchProductMapper = branchProductMapper;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    public BranchProductResponseDTO create (BranchProductRequestDTO dto){
        BranchEntity branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("No existe la sucursal"));
        ProductEntity product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("No existe el producto"));
        if(branchProductRepository.existsByBranch_IdAndProduct_Id(dto.getBranchId(), dto.getProductId())){
            throw new RuntimeException("El producto ya existe en la sucursal");
        }
        BranchProductEntity entity = branchProductMapper.toEntity(dto);
        entity.setBranch(branch);
        entity.setProduct(product);
        BranchProductEntity saved = branchProductRepository.save(entity);
        return branchProductMapper.toDTO(saved);
    }

    public List<BranchProductResponseDTO> findAllByBranchId(Long branchId){
        if(!branchRepository.existsById(branchId)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_Id(branchId);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public List<BranchProductResponseDTO> findAllByBranchName(String branchName){
        if(!branchRepository.existsByName(branchName)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_Name(branchName);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public List<BranchProductResponseDTO> findAvailableByBranch(Long branchId){
        if(!branchRepository.existsById(branchId)){
            throw new RuntimeException("No existe la sucursal");
        }
        List<BranchProductEntity> products = branchProductRepository.findAllByBranch_IdAndAvailableTrue(branchId);
        return products.stream()
                .map(branchProductMapper::toDTO)
                .toList();
    }

    public BranchProductResponseDTO findByBranchAndProduct(Long branchId, Long productId){
        branchRepository.findById(branchId).orElseThrow(() -> new RuntimeException("No existe la sucursal"));
        productRepository.findById(productId).orElseThrow(() -> new RuntimeException("No existe el producto"));
        BranchProductEntity entity = branchProductRepository.findByBranch_IdAndProduct_Id(branchId, productId).
                orElseThrow(() -> new RuntimeException("No se marco el producto en el menu de la sucursal"));
        return branchProductMapper.toDTO(entity);
    }

    public BranchProductResponseDTO update(Long id, BranchProductUpdateDTO dto){
        BranchProductEntity entity = branchProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el producto de sucursal"));
        if(dto.getPrice() != null){
            entity.setPrice(dto.getPrice());
        }
        if(dto.getAvailable() != null){
            entity.setAvailable(dto.getAvailable());
        }
        BranchProductEntity saved = branchProductRepository.save(entity);
        return branchProductMapper.toDTO(saved);
    }

    public void changeAvailability(Long id, Boolean available){
        BranchProductEntity entity = branchProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el producto de sucursal"));
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
        BranchProductEntity entity = branchProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el producto de sucursal"));
        branchProductRepository.delete(entity);
    }

}
