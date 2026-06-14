package com.jll.cibus.orderdetail.service;

import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.branchproduct.service.BranchProductService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.order.service.OrderService;
import com.jll.cibus.order.entity.OrderEntity;
import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailResponseDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailUpdateDTO;
import com.jll.cibus.orderdetail.entity.OrderDetailEntity;
import com.jll.cibus.orderdetail.mapper.OrderDetailMapper;
import com.jll.cibus.orderdetail.repository.OrderDetailRepository;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.repository.ProductRepository;
import com.jll.cibus.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final BranchProductService branchProductService;
    private final ProductRepository productRepository;

    private void validateAvailability(BranchProductEntity productInBranch){
        if(!productInBranch.isAvailable()){
            throw new BusinessException("The product is not available in this branch.");
        }
    }

    public List<OrderDetailResponseDTO> findAll() {                   //probablemente va a quedar sin uso
        List<OrderDetailEntity> orderDetails = orderDetailRepository.findAll();
        return orderDetails.stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    public List<OrderDetailResponseDTO> getByOrderId (Long orderId){
        orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("order",orderId));
        List<OrderDetailEntity> entities = orderDetailRepository.findByOrderId(orderId);
        return entities.stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    private OrderDetailEntity getEntityByOrderIdAndProductId(Long orderId, Long productId){
        if(!orderService.existsById(orderId)){
            throw new ResourceNotFoundException("order ID", orderId);
        }
        if(!productService.existsById(productId)){
            throw new ResourceNotFoundException("product ID", productId);
        }
        return orderDetailRepository.findByOrderIdAndProductId(orderId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("detail of product", productId));
    }

    public OrderDetailResponseDTO getByOrderIdAndProductId (Long orderId, Long productId){
        return orderDetailMapper.toDTO(getEntityByOrderIdAndProductId(orderId, productId));
    }

    @Transactional
    public OrderDetailResponseDTO create(Long orderId, OrderDetailRequestDTO dto) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("order",orderId));
        if(orderService.productExistsInDetails(orderId, dto.getProductId())){
            if(order.getStatus().getName().equalsIgnoreCase("CANCELLED") || order.getStatus().getName().equalsIgnoreCase("PAID")){
                throw new BusinessException("The order "+orderId+" is already closed");
            }
            OrderDetailEntity entity = getEntityByOrderIdAndProductId(orderId, dto.getProductId());
            entity.setQuantity(entity.getQuantity()+dto.getQuantity());
            OrderDetailEntity saved = orderDetailRepository.save(entity);
            orderService.recalculateTotals(orderId);
            return orderDetailMapper.toDTO(saved);
        }
        OrderDetailEntity entity = orderDetailMapper.toEntity(dto);
        ProductEntity product = productRepository.findById(dto.getProductId())
                        .orElseThrow(()->new ResourceNotFoundException("product",dto.getProductId()));
        BranchProductEntity productInBranch = branchProductService.getEntityByBranchAndProduct(order.getBranch().getId(), product.getId());
        validateAvailability(productInBranch);
        entity.setUnitPrice(productInBranch.getPrice());
        entity.setProduct(product);
        entity.setOrder(order);
        if(dto.getObservation() == null){
            entity.setObservation("");                                        //Me parece mejor cadena vacia a que quede un null.
        }
        orderService.changeStatus(orderId, "PREPARING");
        OrderDetailEntity saved = orderDetailRepository.save(entity);
        orderService.recalculateTotals(entity.getOrder().getId());
        return orderDetailMapper.toDTO(saved);
    }

    @Transactional
    public OrderDetailResponseDTO update (Long orderId, Long productId, OrderDetailUpdateDTO dto){
        OrderDetailEntity entity = getEntityByOrderIdAndProductId(orderId, productId);
        OrderEntity order = orderRepository.findById(orderId)
                        .orElseThrow(()->new ResourceNotFoundException("order", orderId));
        entity.setOrder(order);
        /*
        if(dto.getProductId() != null){
            ProductEntity product = productService.getEntity(dto.getProductId());
            BranchProductEntity productInBranch = branchProductService.getEntityByBranchAndProduct(              Yo creo que no se deberia poder cambiar el producto del detalle.
                            entity.getOrder().getBranch().getId(),                                               Un detalle en una orden es decir que producto hay en esa orden, si quiere cambiar el producto que cree otro detalle.
                            product.getId());
            validateAvailability(productInBranch);
            entity.setProduct(product);
            entity.setUnitPrice(productInBranch.getPrice());
        }
         */
        if(dto.getObservation() != null){
            entity.setObservation(dto.getObservation());
        }
        if(dto.getQuantity() != null){
            entity.setQuantity(dto.getQuantity());
        }
        OrderDetailEntity saved = orderDetailRepository.save(entity);
        orderService.recalculateTotals(entity.getOrder().getId());
        return orderDetailMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long orderId, Long productId){
        OrderDetailEntity entity = getEntityByOrderIdAndProductId(orderId, productId);
        orderDetailRepository.delete(entity);
        orderService.recalculateTotals(entity.getOrder().getId());
    }

}
