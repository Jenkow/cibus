package com.jll.cibus.statistics.controller;

import com.jll.cibus.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    /*
    @GetMapping("/branch/{branchId}")
    public ResponseEntity

     */

    // PENDING FEATURES
}
