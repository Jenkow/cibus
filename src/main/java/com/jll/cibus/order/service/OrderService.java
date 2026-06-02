package com.jll.cibus.order.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
import com.jll.cibus.order.repository.OrderStatusRepository;
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
    private void validateOrderRequest (OrderRequestDTO dto)
    {
        if (!userService.existsByDni(dto.getUserDni()))
            throw new ResourceNotFoundException("User dni"+ dto.getUserDni());
        if (!roleValidatorService.isWaiter(dto.getUserDni()))
            throw new BusinessException("The user with id "+ dto.getUserDni() +" is not a waiter");
        if (!tableService.existsById(dto.getTableId()))
            throw new ResourceNotFoundException("Table id ", dto.getTableId());
    }
@Transactional
    public OrderResponseDTO create (OrderRequestDTO dto) {

    validateOrderRequest(dto);
    UserEntity waiter = userService.getEntityByDni(dto.getUserDni());

    //SACAR DEL REQUEST BRANCH ID
    BranchEntity branch = branchService.getEntity(waiter.getBranch().getId());
    TableEntity table = tableService.getTableById(dto.getTableId());

        //VERIFICO QUE EL USUARIO EXISTA, QUE SEA WAITER,  QUE LA TABLE EXISTA


        //VERIFICAR QUE MESA SEA DE ESA BRANCH
        if (!tableService.existsByTableIdAndBranchId(table.getId(),branch.getId()))
            throw new BusinessException("The table n " + table.getId() + "is not from "+ branch.getName());

        //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER
        if (!table.getWaiter().getId().equals(waiter.getDni()))
            throw new BusinessException(waiter.getFirstName()+ "is not working with table n "+ table.getId());

        OrderEntity toCreate = orderMapper.toEntity(dto);
        toCreate.setWaiter(waiter);
        toCreate.setTotal(BigDecimal.ZERO);
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

@Transactional
    public OrderResponseDTO update ( Long orderId, OrderRequestDTO dto) {
        OrderEntity toUpdate = getEntity(orderId);
        validateOrderRequest(dto);
    TableEntity newTable = tableService.getTableById(dto.getTableId());
    UserEntity waiter = userService.getEntityByDni(dto.getUserDni());

        // NO SE DEBERIA PODER CAMBIAR EL USER
        if (!toUpdate.getWaiter().getId().equals(dto.getUserDni()))
            throw new BusinessException("It is not possible to update the waiter");

        //VERIFICO QUE LA MESA CORRESPONDA A LA BRANCH
        if (!tableService.existsByTableIdAndBranchId(newTable.getId(), toUpdate.getBranch().getId()))
             throw new BusinessException("That table is not from branch" + toUpdate.getBranch().getName());

        // VERIFICAR QUE LA MESA ESTE ASIGNADA A ALGUIEN
        if (newTable.getAvailable())
            throw new BusinessException("");

        //VERIFICAR QUE EN ESE MOMENTO LA MESA TENGA ASIGNADO A ESE WAITER
        if (!newTable.getWaiter().getId().equals(waiter.getDni()))
            throw new BusinessException(waiter.getFirstName()+ "is not asigned to table" +newTable.getId());

        toUpdate.setTable(newTable);

        OrderEntity updatedOrder = orderRepository.save(toUpdate);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public void delete(Long orderId)
    {

        //cambiar a baja logica

        OrderEntity order = orderRepository.findById(orderId)
                        .orElseThrow( () -> new ResourceNotFoundException("ID", orderId));

        orderRepository.delete(order);
    }

    public OrderEntity getEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("ID", orderId));
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
