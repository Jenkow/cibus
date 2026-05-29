package com.jll.cibus.table.controller;


import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.table.dto.TableCreateDTO;
import com.jll.cibus.table.dto.TableUpdateDTO;
import com.jll.cibus.table.dto.TableResponseDTO;
import com.jll.cibus.table.service.TableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/{branchId}/tables")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableResponseDTO>> getAll(@PathVariable Long branchId) {
        return ResponseEntity.ok(tableService.findByBranchId(branchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponseDTO> getById(@PathVariable Long branchId, @PathVariable Long id) {
        return ResponseEntity.ok(tableService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TableResponseDTO> create(@PathVariable Long branchId, @Valid @RequestBody TableCreateDTO dto) {
        TableResponseDTO response = tableService.create(dto, branchId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{tableId}/occupy")
    public ResponseEntity<TableResponseDTO> occupy(@PathVariable Long branchId, @PathVariable Long tableId, @RequestBody TableUpdateDTO table) {
        if(table.getWaiterId() == null){
            throw new BusinessException("Waiter ID is needed to ocuppy a table");
        }
        return ResponseEntity.ok(tableService.occupy(tableId, table.getWaiterId()));
    }

    @PatchMapping("/{tableId}/free")
    public ResponseEntity<TableResponseDTO> free(@PathVariable Long branchId, @PathVariable Long tableId) {
        return ResponseEntity.ok(tableService.free(tableId));
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<TableResponseDTO> update(@PathVariable Long branchId, @PathVariable Long tableId, @RequestBody TableUpdateDTO table) {
        return ResponseEntity.ok(tableService.update(tableId, table));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long branchId, @PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
