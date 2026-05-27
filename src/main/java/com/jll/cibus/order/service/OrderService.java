package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.order.OrderStatusRepository;
import com.jll.cibus.order.dto.OrderRequestDTO;
import com.jll.cibus.order.dto.OrderResponseDTO;
import com.jll.cibus.order.entity.OrderEntity;
import com.jll.cibus.order.mapper.OrderMapper;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.table.service.TableService;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    OrderMapper orderMapper;

    private OrderRepository orderRepository;
    private OrderStatusRepository orderStatusRepository;
    private UserService userService;
    private TableService tableService;
    private BranchService branchService;

    public OrderService(OrderMapper orderMapper, OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, UserService userService, TableService tableService, BranchService branchService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.userService = userService;
        this.tableService = tableService;
        this.branchService = branchService;
    }

    public OrderResponseDTO create (OrderRequestDTO dto) {
        UserEntity user = userService.getEntityByDni(dto.getUserDni());
        BranchEntity branch = branchService.getEntity(dto.getBranchId());
        TableEntity table = tableService.getTableById(dto.getTableId());

        OrderEntity toCreate = new OrderEntity();

        toCreate.setWaiter(user);
        toCreate.setBranch(branch);
        toCreate.setTable(table);

        OrderEntity createdOrder = orderRepository.save(toCreate);
        return orderMapper.toDTO(createdOrder);
    }

    public OrderResponseDTO update (OrderRequestDTO dto) {
        UserEntity user = userService.getEntityByDni(dto.getUserDni());
        BranchEntity branch = branchService.getEntity(dto.getBranchId());
        TableEntity table = tableService.getTableById(dto.getTableId());

        OrderEntity toUpdate = new OrderEntity();
        toUpdate.setWaiter(user);
        toUpdate.setBranch(branch);
        toUpdate.setTable(table);

        OrderEntity updatedOrder = orderRepository.save(toUpdate);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public OrderResponseDTO delete(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                        .orElseThrow( () -> new ResourceNotFoundException("ID", orderId));

        orderRepository.delete(order);
        return  orderMapper.toDTO(order);
    }

    public OrderEntity getEntity(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ID", orderId));

        return order;
    }

    public List<OrderResponseDTO> getAll() {
        List<OrderEntity> orders = orderRepository.findAll();

        if(orders.isEmpty()) throw new BusinessException("No order can be found if there is no orders");

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchId(Long branchId) {
        List<OrderEntity> orders = orderRepository.findByBranchId(branchId);

        if (orders.isEmpty()) throw new ResourceNotFoundException("ID", branchId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByTableId(Long tableId) {
        List<OrderEntity> orders = orderRepository.findByTableId(tableId);

        if (orders.isEmpty()) throw new ResourceNotFoundException("ID", tableId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByWaiterId(Long waiterId) {
        List<OrderEntity> orders = orderRepository.findByWaiterId(waiterId);

        if (orders.isEmpty()) throw new ResourceNotFoundException("ID", waiterId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchAndStatus(Long branchId, String status) {
        List<OrderEntity> orders = orderRepository.findByBranch_IdAndStatus_Name(branchId, status);

        if (orders.isEmpty()) throw new ResourceNotFoundException("ID or Status", branchId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByPaid(Boolean paid) {
        List<OrderEntity> orders = orderRepository.findByPaid(paid);

        if (orders.isEmpty()) throw new ResourceNotFoundException("Pay", paid);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }
}
