package com.jll.cibus.statistics.controller;

import com.jll.cibus.statistics.dto.order.OrderInsightDTO;
import com.jll.cibus.statistics.dto.overview.OverviewInsightDTO;
import com.jll.cibus.statistics.dto.product.ProductInsightDTO;
import com.jll.cibus.statistics.dto.table.TableInsightDTO;
import com.jll.cibus.statistics.dto.waiter.WaiterInsightDTO;
import com.jll.cibus.statistics.dto.waiter.WaiterMetricDTO;
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


    @GetMapping("/overview/global")
    public ResponseEntity<OverviewInsightDTO> getGlobalOverviewInsights(@RequestParam LocalDateTime start,
                                                                  @RequestParam LocalDateTime end){


        return ResponseEntity.ok(statisticsService.getGlobalOverviewInsights(start, end));
    }

    @GetMapping("/overview/branch/{branchId}")
    public ResponseEntity<OverviewInsightDTO> getOverviewInsights(@PathVariable Long branchId,
                                                                  @RequestParam LocalDateTime start,
                                                                  @RequestParam LocalDateTime end){

        return ResponseEntity.ok(statisticsService.getOverviewInsights(branchId, start, end));
    }


    @GetMapping("/tables/branch/{branchId}")
    public ResponseEntity<TableInsightDTO> getTableInsights(@PathVariable Long branchId,
                                                            @RequestParam LocalDateTime start,
                                                            @RequestParam LocalDateTime end) {

        return ResponseEntity.ok(statisticsService.getTableInsights(branchId, start, end));
    }

    @GetMapping("/products/branch/{branchId}")
    public ResponseEntity<ProductInsightDTO> getProductInsights(@PathVariable Long branchId,
                                                                @RequestParam LocalDateTime start,
                                                                @RequestParam LocalDateTime end) {

        return ResponseEntity.ok(statisticsService.getProductInsights(branchId, start, end));
    }

    @GetMapping("/products/global")
    public ResponseEntity<ProductInsightDTO> getGlobalProductInsights(@RequestParam LocalDateTime start,
                                                                      @RequestParam LocalDateTime end) {

        return ResponseEntity.ok(statisticsService.getGlobalProductInsights(start, end));
    }

    @GetMapping("/orders/branch/{branchId}")
    public ResponseEntity<OrderInsightDTO> getOrderInsights(@PathVariable Long branchId,
                                                            @RequestParam LocalDateTime start,
                                                            @RequestParam LocalDateTime end){

        return ResponseEntity.ok(statisticsService.getOrderInsights(branchId, start, end));
    }

    @GetMapping("/orders/global")
    public ResponseEntity<OrderInsightDTO> getGlobalOrderInsights(@RequestParam LocalDateTime start,
                                                                  @RequestParam LocalDateTime end){


        return ResponseEntity.ok(statisticsService.getGlobalOrderInsights(start, end));
    }

    @GetMapping("/waiters/branch/{branchId}")
    public ResponseEntity<WaiterInsightDTO> getWaiterInsights(@PathVariable Long branchId,
                                                                  @RequestParam LocalDateTime start,
                                                                  @RequestParam LocalDateTime end){

        return ResponseEntity.ok(statisticsService.getWaiterInsights(branchId, start, end));
    }

    @GetMapping("/waiters/global")
    public ResponseEntity<WaiterInsightDTO> getGlobalWaiterInsights(@RequestParam LocalDateTime start,
                                                              @RequestParam LocalDateTime end){


        return ResponseEntity.ok(statisticsService.getGlobalWaiterInsights(start, end));
    }


}
