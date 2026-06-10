package com.jll.cibus.statistics.controller;

import com.jll.cibus.statistics.dto.table.TableInsightDTO;
import com.jll.cibus.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<TableInsightDTO> getTableInsights(@PathVariable Long branchId,
                                                            @RequestParam LocalDateTime start,
                                                            @RequestParam LocalDateTime end) {

        return  ResponseEntity.ok(statisticsService.getTableInsights(branchId, start, end));
    }

    // PENDING FEATURES
}
