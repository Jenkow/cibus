package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
import com.jll.cibus.order.entity.OrderStatusEntity;
import com.jll.cibus.order.repository.OrderStatusRepository;
import com.jll.cibus.order.dto.OrderRequestDTO;
import com.jll.cibus.order.dto.OrderResponseDTO;
import com.jll.cibus.order.entity.OrderEntity;
import com.jll.cibus.order.mapper.OrderMapper;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.order.specification.OrderSpecification;
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
    private final OrderStatusRepository orderStatusRepository;
    private final UserService userService;
    private final TableService tableService;
    private final BranchService branchService;
    private final RoleValidatorService roleValidatorService;


    public List<OrderResponseDTO> getAll(Long branchId, Long tableId, Long waiterId, String statusName, Boolean paid, LocalDateTime from, LocalDateTime to, BigDecimal minTotal, BigDecimal maxTotal){
        if (from != null && to != null && from.isAfter(to)) {
            throw new BusinessException("The start date cannot be after the end date");
        }
        if (minTotal != null && maxTotal != null && minTotal.compareTo(maxTotal) > 0) {
            throw new BusinessException("The minimum total cannot be greater than the maximum total");
        }
        PredicateSpecification<OrderEntity> spec = PredicateSpecification.allOf(
                OrderSpecification.equalsBranchId(branchId),
                OrderSpecification.equalsTableId(tableId),
                OrderSpecification.equalsWaiterId(waiterId),
                OrderSpecification.equalsStatus(statusName),
                OrderSpecification.equalsPaid(paid),
                OrderSpecification.dateTimeAfter(from),
                OrderSpecification.dateTimeBefore(to),
                OrderSpecification.totalGreaterThanOrEqual(minTotal),
                OrderSpecification.totalLessThanOrEqual(maxTotal)
        );
        return orderRepository.findAll(spec).stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    private void validateOrderRequest(OrderRequestDTO dto) {
        if (!userService.existsById(dto.getUserId()))
            throw new ResourceNotFoundException("User ID" + dto.getUserId());
        if (!roleValidatorService.isWaiter(dto.getUserId()))
            throw new BusinessException("The user with id " + dto.getUserId() + " is not a waiter");
        if (!tableService.existsById(dto.getTableId()))
            throw new ResourceNotFoundException("Table id ", dto.getTableId());
    }

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {

        validateOrderRequest(dto);
        UserEntity waiter = userService.getEntityByDni(dto.getUserId());

        //SACAR DEL REQUEST BRANCH ID
        BranchEntity branch = branchService.getEntity(waiter.getBranch().getId());
        TableEntity table = tableService.getTableById(dto.getTableId());

        //VERIFICO QUE EL USUARIO EXISTA, QUE SEA WAITER,  QUE LA TABLE EXISTA


        //VERIFICAR QUE MESA SEA DE ESA BRANCH
        if (!tableService.existsByTableIdAndBranchId(table.getId(), branch.getId()))
            throw new BusinessException("The table n " + table.getId() + "is not from " + branch.getName());

        //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER
        if (!table.getWaiter().getId().equals(waiter.getDni()))
            throw new BusinessException(waiter.getFirstName() + "is not working with table n " + table.getId());

        OrderEntity toCreate = orderMapper.toEntity(dto);
        toCreate.setWaiter(waiter);
        toCreate.setTotal(BigDecimal.ZERO);
        toCreate.setBranch(branch);
        toCreate.setPaid(false);

        OrderStatusEntity orderStatus = orderStatusRepository.findByName("PREPARING")
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
        toCreate.setStatus(orderStatus);

        //HABRIA QUE INICIALIZAR EL ESTADO TAMBIEN

        OrderEntity createdOrder = orderRepository.save(toCreate);
        return orderMapper.toDTO(createdOrder);
    }

    public boolean existsById(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    @Transactional
    public OrderResponseDTO update(Long orderId, OrderRequestDTO dto) {
        OrderEntity toUpdate = getEntity(orderId);
        validateOrderRequest(dto);
        TableEntity newTable = tableService.getTableById(dto.getTableId());
        UserEntity waiter = userService.getEntityByDni(dto.getUserId());

        // NO SE DEBERIA PODER CAMBIAR EL USER
        if (!toUpdate.getWaiter().getId().equals(dto.getUserId()))
            throw new BusinessException("It is not possible to update the waiter");

        //VERIFICO QUE LA MESA CORRESPONDA A LA BRANCH
        if (!tableService.existsByTableIdAndBranchId(newTable.getId(), toUpdate.getBranch().getId()))
            throw new BusinessException("That table is not from branch" + toUpdate.getBranch().getName());

        // VERIFICAR QUE LA MESA ESTE ASIGNADA A ALGUIEN
        if (newTable.getAvailable())
            throw new BusinessException("Table " + newTable.getId() + " is not occupied");

        //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER
        if (!newTable.getWaiter().getId().equals(waiter.getDni()))
            throw new BusinessException(waiter.getFirstName() + "is not asigned to table" + newTable.getId());

        toUpdate.setTable(newTable);

        OrderEntity updatedOrder = orderRepository.save(toUpdate);
        return orderMapper.toDTO(updatedOrder);
    }

    private void validateTransition(String current, String next) {
        switch (current) {
            case "PREPARING":
                if (!next.equalsIgnoreCase("READY") && !next.equalsIgnoreCase("CANCELLED"))
                    throw new BusinessException("Invalid statis transition");
                break;

            case "READY":
                if (!next.equalsIgnoreCase("SERVED") && !next.equalsIgnoreCase("CANCELLED"))
                    throw new BusinessException("Invalid statis transition");
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

    public List<OrderResponseDTO> findByPaid(Boolean paid) {
        List<OrderEntity> orders = orderRepository.findByPaid(paid);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }
}
