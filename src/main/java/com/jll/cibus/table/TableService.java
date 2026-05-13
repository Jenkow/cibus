package com.jll.cibus.table;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.branch.BranchRepository;
import com.jll.cibus.branch.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.product.ProductEntity;
import com.jll.cibus.user.UserEntity;
import com.jll.cibus.user.UserRepository;
import com.jll.cibus.user.UserRoleEntity;
import jakarta.persistence.Table;
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
    private BranchRepository branchRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BranchService branchService;

    //WHEN USER SERVICE IS DONE AND BRANCH SERVICE IS FIXES WE HAVE TO USE THEM INSTEAD OF REPOSITORIES

    private TableEntity getTableById(Long id)
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
        BranchEntity branch = branchService.getBranchById(branchId);

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
        BranchEntity branch= branchService.getBranchById(branchId);
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
        BranchEntity branch = branchService.getBranchById(branchId);
        UserEntity waiter = userRepository.findById(waiterId)
                .orElseThrow(() -> new RuntimeException("There is no waiter with that ID"));
        List<TableEntity> tables= findByBranchAndWaiter(branch,waiter);

        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }
    // WE MUST VERIFY HAVING THE NECESARY VERIFICATIONS: USER HAS TO BE A WAITER
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
    public TableResponseDTO occupyTable (Long tableId,Long waiterId)
    {
        TableEntity table = getTableById(tableId);

        if (!table.getAvailable())
        {
            throw new BusinessException("The table is already occupied");
        }
        UserEntity waiter= userRepository.findById(waiterId)
                .orElseThrow(()-> new ResourceNotFoundException("waiter",waiterId));

        table.setAvailable(false);
        table.setWaiter(waiter);

        TableEntity updatedTable= tableRepository.save(table);
        return tableMapper.toResponse(updatedTable);
    }

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
    public void delete (Long tableId)
    {
        TableEntity table = getTableById(tableId);
        tableRepository.delete(table);
    }

}
