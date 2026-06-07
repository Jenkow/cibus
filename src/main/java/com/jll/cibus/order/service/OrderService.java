package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
import com.jll.cibus.order.dto.OrderUpdateDTO;
import com.jll.cibus.order.entity.OrderStatusEntity;
import com.jll.cibus.order.repository.OrderStatusRepository;
import com.jll.cibus.order.dto.OrderRequestDTO;
import com.jll.cibus.order.dto.OrderResponseDTO;
import com.jll.cibus.order.entity.OrderEntity;
import com.jll.cibus.order.mapper.OrderMapper;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.order.specification.OrderSpecification;
import com.jll.cibus.orderdetail.entity.OrderDetailEntity;
import com.jll.cibus.orderdetail.mapper.OrderDetailMapper;
import com.jll.cibus.orderdetail.repository.OrderDetailRepository;
import com.jll.cibus.payment.entity.PaymentEntity;
import com.jll.cibus.payment.repository.PaymentRepository;
import com.jll.cibus.table.service.TableService;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final UserService userService;
    private final TableService tableService;
    private final BranchService branchService;
    private final RoleValidatorService roleValidatorService;


    public List<OrderResponseDTO> getAll(Long branchId, Long tableNumber, Long waiterId, String statusName, LocalDateTime from, LocalDateTime to, BigDecimal minTotal, BigDecimal maxTotal){
        if (from != null && to != null && from.isAfter(to)) {
            throw new BusinessException("The start date cannot be after the end date");
        }
        if (minTotal != null && maxTotal != null && minTotal.compareTo(maxTotal) > 0) {
            throw new BusinessException("The minimum total cannot be greater than the maximum total");
        }
        PredicateSpecification<OrderEntity> spec = PredicateSpecification.allOf(
                OrderSpecification.equalsBranchId(branchId),
                OrderSpecification.equalsTableNumber(tableNumber),
                OrderSpecification.equalsWaiterId(waiterId),
                OrderSpecification.equalsStatus(statusName),
                OrderSpecification.dateTimeAfter(from),
                OrderSpecification.dateTimeBefore(to),
                OrderSpecification.totalGreaterThanOrEqual(minTotal),
                OrderSpecification.totalLessThanOrEqual(maxTotal)
        );
        return orderRepository.findAll(spec).stream()
                .map(orderMapper::toDTO)
                .map((dto) -> {
                    setResponseItems(dto);
                    return dto;
                })
                .toList();
    }

    public OrderResponseDTO findById(Long id){
        OrderEntity order = getEntity(id);
        OrderResponseDTO response = orderMapper.toDTO(order);
        setResponseItems(response);
        return response;
    }

    private List<OrderDetailEntity> getItems(Long id){
        return orderDetailRepository.findByOrderId(id);
    }

    private void setResponseItems(OrderResponseDTO order){
        List<OrderDetailEntity> items = getItems(order.getId());
        order.setItems(items.stream()
                .map(orderDetailMapper::toDTO)
                .toList());
    }

    private void validateOrderRequest(Long branchId, OrderRequestDTO dto) {
        if (!userService.existsById(dto.getWaiterId()))
            throw new ResourceNotFoundException("User ID" + dto.getWaiterId());
        if (!roleValidatorService.isWaiter(dto.getWaiterId()))
            throw new BusinessException("The user with id " + dto.getWaiterId() + " is not a waiter");
        if (!tableService.existsByBranchIdAndNumber(branchId, dto.getTableNumber()))
            throw new ResourceNotFoundException("Table number ", dto.getTableNumber());
    }

    @Transactional
    public OrderResponseDTO create(Long branchId, OrderRequestDTO dto) {
        validateOrderRequest(branchId, dto);
        BranchEntity branch = branchService.getEntity(branchId);
        TableEntity table = tableService.getTableByBranchIdAndNumber(branchId, dto.getTableNumber());
        UserEntity waiter = userService.getEntityById(dto.getWaiterId());
        //VERIFICO QUE SEA WAITER
        if(!waiter.getRole().getName().equalsIgnoreCase("waiter")){
            throw new BusinessException("The user "+waiter.getId()+" is not a waiter");
        }
        //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER
        if (table.getWaiter() == null || !table.getWaiter().getId().equals(waiter.getId())) {
            throw new BusinessException(waiter.getFirstName() + " is not working with table n " + table.getNumber());
        }
        OrderEntity order = orderMapper.toEntity(dto);
        order.setBranch(branch);
        order.setTable(table);
        order.setWaiter(waiter);
        OrderStatusEntity orderStatus = orderStatusRepository.findByName("PREPARING")
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        order.setStatus(orderStatus);
        order.setCreatedAt(LocalDateTime.now());
        order.setSubtotal(BigDecimal.ZERO);
        order.setDiscount(BigDecimal.ZERO);
        order.setFinalTotal(BigDecimal.ZERO);
        OrderEntity saved = orderRepository.save(order);
        OrderResponseDTO response = orderMapper.toDTO(saved);
        response.setItems(List.of());
        return orderMapper.toDTO(saved);
    }

    public boolean existsById(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    @Transactional
    public OrderResponseDTO update(Long branchId, Long orderId, OrderUpdateDTO dto) {
        OrderEntity order = getEntity(orderId);
        UserEntity waiter = userService.getEntityById(order.getWaiter().getId());
        if(dto.getTableNumber() != null){
            TableEntity table = tableService.getTableByBranchIdAndNumber(branchId, dto.getTableNumber());
            if(!table.isAvailable()){
                throw new BusinessException("The table "+table.getNumber()+" is occupied");
            }
            //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER
            if (table.getWaiter() == null || !table.getWaiter().getId().equals(waiter.getId()))
                throw new BusinessException(waiter.getFirstName() + " is not asigned to table " + table.getNumber());
            order.setTable(table);
        }
        if(dto.getStatusId() != null){
            OrderStatusEntity status = orderStatusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("OrderStatusId", dto.getStatusId()));
            order.setStatus(status);
        }
        if(dto.getSubtotal() != null){
            order.setSubtotal(dto.getSubtotal());
        }
        if(dto.getDiscount() != null){
            order.setDiscount(dto.getDiscount());
        }
        if(dto.getFinalTotal() != null){
            order.setFinalTotal(dto.getFinalTotal());
        }
        OrderEntity updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    private void validateTransition(String current, String next) {
        switch (current) {
            case "PREPARING":
                if (!next.equalsIgnoreCase("READY") && !next.equalsIgnoreCase("CANCELLED"))
                    throw new BusinessException("Invalid status transition");
                break;

            case "READY":
                if (!next.equalsIgnoreCase("SERVED") && !next.equalsIgnoreCase("CANCELLED"))
                    throw new BusinessException("Invalid status transition");
                break;
            case "SERVED":
                if (!next.equalsIgnoreCase("PAID") && !next.equalsIgnoreCase("CANCELLED"))
                    throw new BusinessException("Invalid status transition");
                break;
            case "PAID":
            case "CANCELLED":
                throw new BusinessException("Order can no longer change status");
        }
    }

    public List<String> getStatuses(){
        return orderStatusRepository.findAll().stream()
                .map(OrderStatusEntity::getName)
                .toList();
    }

    @Transactional
    public OrderResponseDTO changeStatus(Long orderId, String newStatus) {
        OrderEntity order = getEntity(orderId);
        String currentStatus = order.getStatus().getName();
        OrderStatusEntity orderStatus = orderStatusRepository.findByName(newStatus)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        validateTransition(currentStatus, newStatus);

        order.setStatus(orderStatus);
        OrderEntity updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long orderId) {
        changeStatus(orderId, "CANCELLED");
    }

    public OrderEntity getEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ID", orderId));
    }

    @Transactional
    public void recalculateTotals(Long id){
        OrderEntity order = getEntity(id);
        BigDecimal subtotal = getItems(id).stream()
                .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);
        order.setFinalTotal(subtotal.subtract(order.getDiscount()));
        orderRepository.save(order);
    }

    public Boolean productExistsInDetails(Long orderId, Long productId){
        return getItems(orderId).stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

/*
    public List<OrderResponseDTO> findByBranchId(Long branchId) {
        if (!branchService.existsById(branchId))
            throw new ResourceNotFoundException("Branch id " + branchId);

        List<OrderEntity> orders = orderRepository.findByBranchId(branchId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchIdAndTableId(Long branchId, Long tableId) {
        if (!branchService.existsById(branchId))
            throw new ResourceNotFoundException("Branch id " + branchId);
        if (!tableService.existsById(tableId))
            throw new ResourceNotFoundException("Table id " + tableId);

        List<OrderEntity> orders = orderRepository.findByBranchIdAndTableId(branchId, tableId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchIdAndWaiterId(Long branchId, Long waiterId) {

        if (!branchService.existsById(branchId))
            throw new ResourceNotFoundException("Branch id " + branchId);
        if (!userService.existsByDni(waiterId))
            throw new ResourceNotFoundException("User id " + waiterId);
        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user with id " + waiterId + " is not a waiter");

        List<OrderEntity> orders = orderRepository.findByBranchIdAndWaiterId(branchId, waiterId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchAndStatus(Long branchId, String status) {
        if (!branchService.existsById(branchId))
            throw new ResourceNotFoundException("BRANCH ID", branchId);

        List<OrderEntity> orders = orderRepository.findByBranch_IdAndStatus_Name(branchId, status);
        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByPaid(Long branchId, Boolean paid) {
        List<OrderEntity> orders = orderRepository.findByBranch_IdAndStatus_Name(branchId, "PAID");

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

 */
}
