package com.jll.cibus.statistics.service;

import com.jll.cibus.branchproduct.repository.BranchProductRepository;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.orderdetail.repository.OrderDetailRepository;
import com.jll.cibus.statistics.dto.table.TableInsightDTO;
import com.jll.cibus.statistics.dto.table.TableMetricDTO;
import com.jll.cibus.table.repository.TableRepository;
import com.jll.cibus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    //-------- PARA APROVECHAR AL 100% ESTA FEATURE ES NECESARIO TENER LOGGERS DE TODOS LOS SERVICIOS.

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final BranchProductRepository branchProductRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;


    public TableInsightDTO getTableInsights(Long branchId, LocalDateTime start, LocalDateTime end) {

        //  Mesa más concurrida en el rango de tiempo solicitado.
        TableMetricDTO mostBusy =
                orderRepository.findMostBusyTable(branchId, start, end)
                        .stream()
                        .findFirst()
                        .orElse(null);

        // Mesa menos concurrida en el rango de tiempo solicitado.
        TableMetricDTO lessBusy =
                orderRepository.findLessBusyTable(branchId, start, end)
                        .stream()
                        .findFirst()
                        .orElse(null);

        // Porcentaje de ocupación en el rango de tiempo solicitado.
        Long occupiedTables =
                orderRepository.countOccupiedTables(branchId, start, end);

        Long totalTables =
                tableRepository.countByBranchId(branchId);

        Double occupancyRate =
                totalTables == 0 ? 0.0 : occupiedTables.doubleValue() * 100 / totalTables;

        return TableInsightDTO.builder()
                .mostBusyTable(mostBusy)
                .lessBusyTable(lessBusy)
                .occupancyRate(occupancyRate)
                .build();
    }
}
