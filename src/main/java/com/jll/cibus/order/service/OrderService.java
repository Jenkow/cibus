package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
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
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    OrderMapper orderMapper;

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final UserService userService;
    private final TableService tableService;
    private final BranchService branchService;
    private final RoleValidatorService roleValidatorService;

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

    validateOrderRequest(dto);
    //VERIFICAR QUE MESA EXISTA
    // VERIFICAR QUE EL WAITER TRABAJE EN ESA BRANCH
    //VERIFICAR QUE MESA SEA DE ESA BRANCH
    //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER


        OrderEntity toCreate = orderMapper.toEntity(dto);
        UserEntity waiter = userService.getEntityByDni(dto.getUserDni());
        toCreate.setWaiter(waiter);
        toCreate.setPaid(false);
        toCreate.setTotal(BigDecimal.ZERO);
        BranchEntity branch = branchService.getEntity(dto.getBranchId());
        toCreate.setBranch(branch);
        toCreate.setPaid(false);
        //HABRIA QUE INICIALIZAR EL ESTADO TAMBIEN

        OrderEntity createdOrder = orderRepository.save(toCreate);
        return orderMapper.toDTO(createdOrder);
    }
    public boolean existsById (Long orderId)
    {
        return orderRepository.existsById(orderId);
    }
    private void validateOrderRequest (OrderRequestDTO dto)
    {
        if (!userService.existsByDni(dto.getUserDni()))
            throw new ResourceNotFoundException("User dni"+ dto.getUserDni());
        if (!roleValidatorService.isWaiter(dto.getUserDni()))
            throw new BusinessException("The user with id "+ dto.getUserDni() +" is not a waiter");
        if (!branchService.existsById(dto.getBranchId()))
            throw new ResourceNotFoundException("Branch id ", dto.getBranchId());
    }
@Transactional
    public OrderResponseDTO update ( Long orderId, OrderRequestDTO dto) {
        if (!existsById(orderId))
             throw new ResourceNotFoundException("order id", orderId);

        validateOrderRequest(dto);
        // NO SE DEBERIA PODER CAMBIAR LA BRANCH
        // VERIFICAR QUE LA MESA ESTE ASIGNADA A ALGUIEN
        // INFORMACION DEL USUARIO HAY QUE SACARLA DE LA TABLE EN REALIDAD
        //VERIFICAR QUE MESA EXISTA
        // VERIFICAR QUE EL WAITER TRABAJE EN ESA BRANCH
        //VERIFICAR QUE MESA SEA DE ESA BRANCH
        //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER

        OrderEntity toUpdate = getEntity(orderId);


        UserEntity user = userService.getEntityByDni(dto.getUserDni());
        BranchEntity branch = branchService.getEntity(dto.getBranchId());
        TableEntity table = tableService.getTableById(dto.getTableId());

        toUpdate.setWaiter(user);
        toUpdate.setBranch(branch);
        toUpdate.setTable(table);

        //CREO QUE HAY QUE CAMBIAR O EL MAPPER O EL REQUEST

        OrderEntity updatedOrder = orderRepository.save(toUpdate);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long orderId) {

        //cambiar a baja logica

        OrderEntity order = orderRepository.findById(orderId)
                        .orElseThrow( () -> new ResourceNotFoundException("ID", orderId));

        orderRepository.delete(order);
    }

    public OrderEntity getEntity(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ID", orderId));

        return order;
    }

    public List<OrderResponseDTO> getAll() {
        List<OrderEntity> orders = orderRepository.findAll();

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByBranchId(Long branchId) {
        if (!branchService.existsById(branchId))
            throw new ResourceNotFoundException("Branch id "+ branchId);

        List<OrderEntity> orders = orderRepository.findByBranchId(branchId);

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByTableId(Long tableId) {
        List<OrderEntity> orders = orderRepository.findByTableId(tableId);


        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    public List<OrderResponseDTO> findByWaiterId(Long waiterId) {

        if (!userService.existsByDni(waiterId))
            throw new ResourceNotFoundException("User id "+ waiterId);
        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user with id "+ waiterId+ "is not a waiter");

        List<OrderEntity> orders = orderRepository.findByWaiterId(waiterId);

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
