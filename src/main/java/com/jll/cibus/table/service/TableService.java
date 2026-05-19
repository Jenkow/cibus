package com.jll.cibus.table.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
import com.jll.cibus.table.dto.TableRequestDTO;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.table.mapper.TableMapper;
import com.jll.cibus.table.repository.TableRepository;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService
{
    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private TableMapper tableMapper;
    @Autowired
    private BranchService branchService;
    @Autowired
    private RoleValidatorService roleValidatorService;
    @Autowired
    private UserService userService;


    public TableEntity getTableById(Long id)
    {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("table", id));
    }
    private List<TableEntity> findByBranch (BranchEntity branch)
    {
       return tableRepository.findByBranch(branch);
    }

    public List<TableResponseDTO> findByBranchId (Long branchId)
    {
        BranchEntity branch = branchService.getEntity(branchId);

        List<TableEntity> tables = findByBranch(branch);

        return  tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }

    private List<TableEntity> findByBranchAndAvailable( BranchEntity branch, boolean available)
    {
        return tableRepository.findByBranchAndAvailable(branch, available);
    }
    public List<TableResponseDTO> findByBranchIdAndAvailable (Long branchId, boolean available)
    {
        BranchEntity branch= branchService.getEntity(branchId);
        List<TableEntity> tables = findByBranchAndAvailable(branch, available);
        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }
    private List<TableEntity> findByBranchAndWaiter (BranchEntity branch, UserEntity waiter)
    {
        return tableRepository.findByBranchAndWaiter(branch,waiter);
    }
    public List<TableResponseDTO> findByBranchIdAndWaiterId (Long branchId, Long waiterId)
    {
        BranchEntity branch = branchService.getEntity(branchId);

        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user is not a waiter");
        UserEntity waiter= userService.getEntityByDni(waiterId);

        List<TableEntity> tables= findByBranchAndWaiter(branch,waiter);

        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }
    @Transactional
    public TableResponseDTO createTable(TableRequestDTO dto, Long branchId)
    {
        TableEntity tableEntity = tableMapper.toEntity(dto, branchId);
        TableEntity savedTable = tableRepository.save(tableEntity);
        return tableMapper.toResponse(savedTable);
    }

    public List<TableResponseDTO> findAll ()
    {
        List<TableEntity> tables = tableRepository.findAll();
        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }
    public TableResponseDTO findById(Long tableId)
    {
        TableEntity table = getTableById(tableId);
        return tableMapper.toResponse(table);
    }
    @Transactional
    public TableResponseDTO occupyTable (Long tableId,Long waiterId)
    {
        TableEntity table = getTableById(tableId);

        if (!table.getAvailable())
            throw new BusinessException("The table is already occupied");
        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user is not a waiter");

        UserEntity waiter= userService.getEntityByDni(waiterId);

        table.setAvailable(false);
        table.setWaiter(waiter);

        TableEntity updatedTable= tableRepository.save(table);

        return tableMapper.toResponse(updatedTable);
    }
    @Transactional
    public TableResponseDTO freeTable(Long tableId)
    {
        TableEntity table = getTableById(tableId);

        if (table.getAvailable())
        {
            throw new BusinessException("The table is already free");
        }
        table.setAvailable(true);
        table.setWaiter(null);

        TableEntity updatedTable = tableRepository.save(table);
        return tableMapper.toResponse(updatedTable);
    }
    @Transactional
    public TableResponseDTO updateCapacity (Long tableId, Integer capacity )
    {
        TableEntity table = getTableById(tableId);
        if (capacity<1)
        {
            throw new BusinessException("The capacity can not be less than 1");
        }
        table.setCapacity(capacity);
        TableEntity updatedTable = tableRepository.save(table);
        return tableMapper.toResponse(updatedTable);
    }
    @Transactional
    public void delete (Long tableId)
    {
        TableEntity table = getTableById(tableId);
        tableRepository.delete(table);
    }

}
