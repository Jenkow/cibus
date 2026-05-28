package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.InvalidCredentialsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
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
    private RoleValidatorService roleValidatorService;

    public OrderService(OrderMapper orderMapper, OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, UserService userService, TableService tableService, BranchService branchService, RoleValidatorService roleValidatorService) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.userService = userService;
        this.tableService = tableService;
        this.branchService = branchService;
        this.roleValidatorService =  roleValidatorService;
    }
@Transactional
    public OrderResponseDTO create (OrderRequestDTO dto) {

        //        if (!userService.existsByDNI (dto.getUserDni()))
//            throw new ResourceNotFoundException()
//        if (branchService.existsById(dto.getBranchId()))
//            throw new ResourceNotFoundException("Branch id",dto.getBranchId());
//      PARA LA VALIDACION DE TABLE PRIMERO VAMOS A TENER QUE RE CHEQUEAR EL TEMA DE LOS ID DE TABLES, Y VER SI HAY QUE CHEQUEAR QUE LA MESA CORRESPONDA EN ESE MOMENTO AL USUARIO ASIGNADO

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
    public boolean existsById (Long orderId)
    {
        return orderRepository.existsById(orderId);
    }
@Transactional
    public OrderResponseDTO update ( Long orderId, OrderRequestDTO dto) {

        //        if (!userService.existsByDNI (dto.getUserDni()))
//            throw new ResourceNotFoundException()
//        if (branchService.existsById(dto.getBranchId()))
//            throw new ResourceNotFoundException("Branch id",dto.getBranchId());
//      PARA LA VALIDACION DE TABLE PRIMERO VAMOS A TENER QUE RE CHEQUEAR EL TEMA DE LOS ID DE TABLES, Y VER SI HAY QUE CHEQUEAR QUE LA MESA CORRESPONDA EN ESE MOMENTO AL USUARIO ASIGNADO

        if (!existsById(orderId))
            throw new ResourceNotFoundException("order id", orderId);
        OrderEntity toUpdate = getEntity(orderId);

        //me genera dudas que se use entity
        UserEntity user = userService.getEntityByDni(dto.getUserDni());
        BranchEntity branch = branchService.getEntity(dto.getBranchId());
        TableEntity table = tableService.getTableById(dto.getTableId());

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
//        if (!userService.existsByDni (waiterId))
//        ......
        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user with id "+ waiterId+ "is not a waiter");

        List<OrderEntity> orders = orderRepository.findByWaiterId(waiterId);

        if (orders.isEmpty()) throw new ResourceNotFoundException("ID", waiterId); // acá, no deberia mostrar la lista vacia?

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchAndStatus(Long branchId, String status) {
        if (!branchService.existsById(branchId))
            throw new ResourceNotFoundException("BRANCH ID", branchId);

        List<OrderEntity> orders = orderRepository.findByBranch_IdAndStatus_Name(branchId, status);

        if (orders.isEmpty()) throw new ResourceNotFoundException("ID or Status", branchId); // acá, no deberia mostrar la lista vacia?

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByPaid(Boolean paid) {
        List<OrderEntity> orders = orderRepository.findByPaid(paid);

        if (orders.isEmpty()) throw new ResourceNotFoundException("Pay", paid); // acá, no deberia mostrar la lista vacia?

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }
}
