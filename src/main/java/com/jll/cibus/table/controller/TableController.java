package com.jll.cibus.table.controller;


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
    public ResponseEntity<TableResponseDTO> createTable(@PathVariable Long branchId, @Valid @RequestBody TableCreateDTO dto) {
        TableResponseDTO response = tableService.create(dto, branchId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{tableId}/occupy")
    public ResponseEntity<TableResponseDTO> occupyTable(@PathVariable Long branchId, @RequestParam Long waiterId, @PathVariable Long tableId) {
        return ResponseEntity.ok(tableService.occupyTable(tableId, waiterId));
    }

    @PutMapping("/{tableId}/free")
    public ResponseEntity<TableResponseDTO> freeTable(@PathVariable Long branchId, @PathVariable Long tableId) {
        return ResponseEntity.ok(tableService.freeTable(tableId));
    }

    @PutMapping("/{tableId}/capacity")
    public ResponseEntity<TableResponseDTO> updateCapacity(@PathVariable Long branchId, @PathVariable Long tableId, @RequestParam Integer capacity) {
        return ResponseEntity.ok(tableService.updateCapacity(tableId, capacity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long branchId, @PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
