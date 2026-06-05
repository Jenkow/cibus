package com.jll.cibus.table.service;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.service.BranchService;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.common.service.RoleValidatorService;
import com.jll.cibus.table.dto.TableCreateDTO;
import com.jll.cibus.table.dto.TableUpdateDTO;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.table.mapper.TableMapper;
import com.jll.cibus.table.repository.TableRepository;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final TableMapper tableMapper;
    private final BranchService branchService;
    private final RoleValidatorService roleValidatorService;
    private final UserService userService;


    public TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("table", id));
    }

    public TableEntity getTableByBranchIdAndNumber(Long branchId, Integer number) {
        return tableRepository.findByBranch_IdAndNumber(branchId, number)
                .orElseThrow(() -> new ResourceNotFoundException("table number", number));
    }

    public List<TableResponseDTO> findByBranchId(Long branchId) {
        List<TableEntity> tables = tableRepository.findByBranchId(branchId);
        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }

    public Boolean existsById(Long id) {
        return tableRepository.existsById(id);
    }

    public Boolean existsByBranchIdAndNumber(Long branchId, Integer number){
        return tableRepository.existsByBranchIdAndNumber(branchId, number);
    }

    public Boolean existsByTableIdAndBranchId(Long tableId, Long branchId) {
        return tableRepository.existsByIdAndBranchId(tableId, branchId);
    }

    @Transactional
    public TableResponseDTO create(TableCreateDTO dto, Long branchId) {
        if(tableRepository.existsByBranchIdAndNumber(branchId, dto.getNumber())){
            throw new BusinessException("A table with number "+ dto.getNumber() +" already exists in the branch.");
        }
        TableEntity table = tableMapper.toEntity(dto);
        table.setBranch(branchService.getEntity(branchId));
        table.setAvailable(Boolean.TRUE);
        table.setWaiter(null);
        TableEntity saved = tableRepository.save(table);
        return tableMapper.toResponse(saved);
    }

    public TableResponseDTO findById(Long tableId) {
        TableEntity table = getTableById(tableId);
        return tableMapper.toResponse(table);
    }

    public TableResponseDTO findByBranchIdAndNumber(Long branchId, Integer number) {
        TableEntity table = tableRepository.findByBranch_IdAndNumber(branchId, number)
                .orElseThrow(() -> new ResourceNotFoundException("There is no table "+number+ " in branch "+branchId));
        return tableMapper.toResponse(table);
    }

    @Transactional
    public TableResponseDTO occupy(Long branchId, Integer tableNumber, Long waiterId) {
        TableEntity table = getTableByBranchIdAndNumber(branchId, tableNumber);

        if (!table.getAvailable())
            throw new BusinessException("The table is already occupied");
        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user is not a waiter");

        UserEntity waiter = userService.getEntityByDni(waiterId);

        table.setAvailable(false);
        table.setWaiter(waiter);

        TableEntity updatedTable = tableRepository.save(table);

        return tableMapper.toResponse(updatedTable);
    }

    @Transactional
    public TableResponseDTO free(Long branchId, Integer tableNumber) {
        TableEntity table = getTableByBranchIdAndNumber(branchId, tableNumber);

        if (table.getAvailable()) {
            throw new BusinessException("The table is already free");
        }
        table.setAvailable(true);
        table.setWaiter(null);

        TableEntity updatedTable = tableRepository.save(table);
        return tableMapper.toResponse(updatedTable);
    }

    @Transactional
    public TableResponseDTO update(Long branchId, Integer tableNumber, TableUpdateDTO newTable) {
        TableEntity table = getTableByBranchIdAndNumber(branchId, tableNumber);
        if(newTable.getNumber() != null){
            table.setNumber(newTable.getNumber());
        }
        if (newTable.getCapacity() != null) {
            if (newTable.getCapacity() < 1) {
                throw new BusinessException("The capacity can not be less than 1");
            }
            table.setCapacity(newTable.getCapacity());
        }
        if (newTable.getWaiterId() != null) {
            UserEntity waiter = userService.getEntityById(newTable.getWaiterId());           //   FALTA getEntityById en UserService
            table.setWaiter(waiter);
        }

        TableEntity updatedTable = tableRepository.save(table);
        return tableMapper.toResponse(updatedTable);
    }

    @Transactional
    public void delete(Long branchId, Integer tableNumber) {
        TableEntity table = getTableByBranchIdAndNumber(branchId, tableNumber);
        tableRepository.delete(table);
    }


    //------------------------------------------------------------- TODOS ESTOS METODOS ESTABAN HECHOS PERO NO PENSAMOS NINGUN ENDPOINT PARA ELLOS, BORRARLOS O APLICAR ENDPONTS   ---------------------------------------------------------------------------------------

/*
    public List<TableResponseDTO> findAll() {
        List<TableEntity> tables = tableRepository.findAll();
        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }

    private List<TableEntity> findByBranch(BranchEntity branch) {
            return tableRepository.findByBranch(branch);
        }

        public List<TableResponseDTO> findByBranchId(Long branchId) {
            BranchEntity branch = branchService.getEntity(branchId);

            List<TableEntity> tables = findByBranch(branch);

            return tables.stream()
                    .map(tableMapper::toResponse)
                    .toList();
        }

    private List<TableEntity> findByBranchAndAvailable(BranchEntity branch, boolean available) {
        return tableRepository.findByBranchAndAvailable(branch, available);
    }

    public List<TableResponseDTO> findByBranchIdAndAvailable(Long branchId, boolean available) {
        BranchEntity branch = branchService.getEntity(branchId);
        List<TableEntity> tables = findByBranchAndAvailable(branch, available);
        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }

    private List<TableEntity> findByBranchAndWaiter(BranchEntity branch, UserEntity waiter) {
        return tableRepository.findByBranchAndWaiter(branch, waiter);
    }

    public List<TableResponseDTO> findByBranchIdAndWaiterId(Long branchId, Long waiterId) {
        BranchEntity branch = branchService.getEntity(branchId);

        if (!roleValidatorService.isWaiter(waiterId))
            throw new BusinessException("The user is not a waiter");
        UserEntity waiter = userService.getEntityByDni(waiterId);

        List<TableEntity> tables = findByBranchAndWaiter(branch, waiter);

        return tables.stream()
                .map(tableMapper::toResponse)
                .toList();
    }
*/

}
