package com.jll.cibus.orderdetail.service;

import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.branchproduct.service.BranchProductService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.order.service.OrderService;
import com.jll.cibus.order.entity.OrderEntity;
import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailResponseDTO;
import com.jll.cibus.orderdetail.entity.OrderDetailEntity;
import com.jll.cibus.orderdetail.mapper.OrderDetailMapper;
import com.jll.cibus.orderdetail.repository.OrderDetailRepository;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final ProductService productService;
    private final OrderService orderService;
    private final BranchProductService branchProductService;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, OrderDetailMapper orderDetailMapper, ProductService productService, OrderService orderService, BranchProductService branchProductService){
        this.orderDetailRepository = orderDetailRepository;
        this.orderDetailMapper = orderDetailMapper;
        this.productService = productService;
        this.orderService = orderService;
        this.branchProductService = branchProductService;
    }

    private void validateAvailability(BranchProductEntity productInBranch){
        if(!productInBranch.isAvailable()){
            throw new BusinessException("El producto no está disponible en esta sucursal");
        }
    }

    public List<OrderDetailResponseDTO> findAll() {                   //probablemente va a quedar sin uso
        List<OrderDetailEntity> orderDetails = orderDetailRepository.findAll();
        return orderDetails.stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    private OrderDetailEntity getEntity(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("order detail", id));
    }

    public OrderDetailResponseDTO getById(Long orderId, Long id) {
        OrderDetailEntity entity = getEntity(id);
       /* if(orderService.existsById){            FALTA METODO EXISTBYID EN ORDERSERVICE
            throw new BusinessException("No existe la orden " + orderId);
        }*/
        if(entity.getOrder().getId()!=orderId){
            throw new BusinessException("El detalle " + id + " no pertenece a la orden " + orderId);
        }
        return orderDetailMapper.toDTO(entity);
    }

    public List<OrderDetailResponseDTO> getByOrderId (Long orderId){
        orderService.getEntity(orderId);
        List<OrderDetailEntity> entities = orderDetailRepository.findByOrderId(orderId);
        return entities.stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    public OrderDetailEntity getByOrderIdAndProductId (Long orderId, Long productId){
        return orderDetailRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un detalle para la orden " + orderId + " y el producto " + productId));
    }

    @Transactional
    public OrderDetailResponseDTO create(Long orderId, OrderDetailRequestDTO dto) {
        OrderDetailEntity entity = orderDetailMapper.toEntity(dto);
        ProductEntity product = productService.getEntity(dto.getProductId());
        OrderEntity order = orderService.getEntity(orderId);
        BranchProductEntity productInBranch = branchProductService.getEntityByBranchAndProduct(order.getBranch().getId(), product.getId());
        validateAvailability(productInBranch);
        entity.setUnitPrice(productInBranch.getPrice());
        entity.setProduct(product);
        entity.setOrder(order);
        OrderDetailEntity saved = orderDetailRepository.save(entity);
        return orderDetailMapper.toDTO(saved);
    }

    @Transactional
    public OrderDetailResponseDTO update (Long orderId, Long id, OrderDetailRequestDTO dto){
        OrderDetailEntity entity = getEntity(id);
        OrderEntity order = orderService.getEntity(orderId);
        entity.setOrder(order);
        if(dto.getProductId() != null){
            ProductEntity product = productService.getEntity(dto.getProductId());
            BranchProductEntity productInBranch = branchProductService.getEntityByBranchAndProduct(
                            entity.getOrder().getBranch().getId(),
                            product.getId());
            validateAvailability(productInBranch);
            entity.setProduct(product);
            entity.setUnitPrice(productInBranch.getPrice());
        }
        if(dto.getObservation() != null){
            entity.setObservation(dto.getObservation());
        }
        if(dto.getQuantity() != null){
            entity.setQuantity(dto.getQuantity());
        }
        OrderDetailEntity saved = orderDetailRepository.save(entity);
        return orderDetailMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long orderId, Long id){
        OrderDetailEntity entity = getEntity(id);
        /* if(orderService.existsById){            FALTA METODO EXISTBYID EN ORDERSERVICE
            throw new BusinessException("No existe la orden " + orderId);
        }*/
        if(entity.getOrder().getId()!=orderId){
            throw new BusinessException("El detalle " + id + " no pertenece a la orden " + orderId);
        }
        orderDetailRepository.delete(entity);
    }

}
