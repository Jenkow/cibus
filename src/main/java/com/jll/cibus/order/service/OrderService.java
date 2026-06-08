package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
import com.jll.cibus.order.dto.OrderStatusDTO;
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
import com.jll.cibus.payment.dto.DiscountRequestDTO;
import com.jll.cibus.payment.dto.PaymentDTO;
import com.jll.cibus.payment.entity.OrderPaymentEntity;
import com.jll.cibus.payment.entity.PaymentMethodEntity;
import com.jll.cibus.payment.repository.OrderPaymentRepository;
import com.jll.cibus.payment.repository.PaymentMethodRepository;
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
    private final OrderStatusService orderStatusService;
    private final UserService userService;
    private final TableService tableService;
    private final BranchService branchService;
    private final RoleValidatorService roleValidatorService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderPaymentRepository orderPaymentRepository;


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
                .peek(this::setResponseItems)
                .peek(this::setRemainingAmount)
                .toList();
    }

    public OrderResponseDTO findById(Long id){
        OrderEntity order = getEntity(id);
        OrderResponseDTO response = orderMapper.toDTO(order);
        setResponseItems(response);
        setRemainingAmount(response);
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
        OrderStatusEntity orderStatus = orderStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        order.setStatus(orderStatus);
        order.setCreatedAt(LocalDateTime.now());
        order.setSubtotal(BigDecimal.ZERO);
        order.setDiscount(BigDecimal.ZERO);
        order.setFinalTotal(BigDecimal.ZERO);
        OrderEntity saved = orderRepository.save(order);
        OrderResponseDTO response = orderMapper.toDTO(saved);
        response.setItems(List.of());
        setRemainingAmount(response);
        return response;
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
        OrderResponseDTO response = orderMapper.toDTO(updatedOrder);
        setRemainingAmount(response);
        return response;
    }

    public List<OrderStatusDTO> getStatuses(){
        return orderStatusService.findAll();
    }

    public OrderResponseDTO changeStatus(Long orderId, String newStatus){
        OrderEntity order = getEntity(orderId);
        orderStatusService.changeOrderStatus(order, newStatus);
        OrderEntity updatedOrder = orderRepository.save(order);
        OrderResponseDTO response = orderMapper.toDTO(updatedOrder);
        setRemainingAmount(response);
        return response;
    }

    private List<OrderPaymentEntity> getPayments(Long orderId){
        return orderPaymentRepository.findByOrder_Id(orderId);
    }

    public void setRemainingAmount(OrderResponseDTO dto) {                             //metodo para calcular y asignar al responseDTO lo que falta pagar de la orden.
        BigDecimal totalPaid = getPayments(dto.getId())
                .stream()
                .map(OrderPaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setRemainingAmount(dto.getFinalTotal().subtract(totalPaid));
    }

    public Boolean isCancelled(OrderEntity order){
        return order.getStatus().getName().equalsIgnoreCase("CANCELLED");
    }

    public Boolean isPaid(OrderEntity order){
        return order.getStatus().getName().equalsIgnoreCase("PAID");
    }

    @Transactional
    public PaymentDTO addPayment(Long orderId, PaymentDTO payment){
        OrderEntity order = getEntity(orderId);
        if(isCancelled(order)){
            throw new BusinessException("The order "+orderId+" is cancelled");
        }
        if(isPaid(order)){
            throw new BusinessException("The order "+orderId+" is already paid");
        }
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
        BigDecimal totalPaid = getPayments(orderId)
                .stream()
                .map(OrderPaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAmount = order.getFinalTotal().subtract(totalPaid);
        if(payment.getAmount().compareTo(remainingAmount) > 0){
            throw new BusinessException("Payment amount exceeds remaining balance");
        }
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findById(payment.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("payment method ID", payment.getPaymentMethodId()));
        OrderPaymentEntity paymentEntity = OrderPaymentEntity.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .amount(payment.getAmount())
                .build();
        OrderPaymentEntity saved = orderPaymentRepository.save(paymentEntity);
        if (totalPaid.add(paymentEntity.getAmount()).compareTo(order.getFinalTotal()) >= 0) {
            OrderStatusEntity paidStatus = orderStatusRepository.findByName("PAID")
                            .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            order.setStatus(paidStatus);
            order.setClosedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
        return PaymentDTO.builder()
                .paymentMethodId(saved.getPaymentMethod().getId())
                .amount(saved.getAmount())
                .build();
    }

    @Transactional
    public OrderResponseDTO applyDiscount(Long orderId, DiscountRequestDTO discount){
        OrderEntity order = getEntity(orderId);
        if(isCancelled(order)){
            throw new BusinessException("The order "+orderId+" is cancelled");
        }
        if(isPaid(order)){
            throw new BusinessException("The order "+orderId+" is already paid");
        }
        if(discount.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Discount must be greater than zero");
        }
        if(discount.getAmount().compareTo(order.getFinalTotal()) > 0){
            throw new BusinessException("Discount cannot exceed final total");
        }
        order.setDiscount(order.getDiscount().add(discount.getAmount()));
        order.setFinalTotal(order.getFinalTotal().subtract(discount.getAmount()));
        OrderEntity saved = orderRepository.save(order);
        OrderResponseDTO response = orderMapper.toDTO(saved);
        return response;
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

}
