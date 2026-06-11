package com.jll.cibus.statistics.service;

import com.jll.cibus.branchproduct.repository.BranchProductRepository;
import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.orderdetail.repository.OrderDetailRepository;
import com.jll.cibus.statistics.dto.product.ProductCategoryMetricDTO;
import com.jll.cibus.statistics.dto.product.ProductInsightDTO;
import com.jll.cibus.statistics.dto.product.ProductMetricDTO;
import com.jll.cibus.statistics.dto.table.TableInsightDTO;
import com.jll.cibus.statistics.dto.table.TableMetricDTO;
import com.jll.cibus.table.repository.TableRepository;
import com.jll.cibus.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

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

        // Calculo de porcentaje de ocupación en el rango de tiempo solicitado.
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

    public ProductInsightDTO getProductInsights(Long branchId, LocalDateTime start, LocalDateTime end) {

        // Producto más vendido.
        ProductMetricDTO mostPopularProduct = orderDetailRepository.findMostPopularProducts(branchId, start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Producto menos vendido.
        ProductMetricDTO lessPopularProduct = orderDetailRepository.findLessPopularProducts(branchId, start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Producto mas caro.
        ProductMetricDTO mostExpensiveProduct = orderDetailRepository.findMostExpensiveProducts(branchId, start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Producto más barato.
        ProductMetricDTO cheapestProduct = orderDetailRepository.findCheapestProducts(branchId, start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Categoria mas vendida
        ProductCategoryMetricDTO mostPopularCategory = orderDetailRepository.findMostPopularCategories(branchId, start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Categoria menos vendida
        ProductCategoryMetricDTO lessPopularCategory = orderDetailRepository.findLessPopularCategories(branchId, start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        return ProductInsightDTO.builder() // Patron builder para construir el DTO del insight y devolverlo.
                .mostPopularProduct(mostPopularProduct)
                .lessPopularProduct(lessPopularProduct)
                .mostExpensiveProduct(mostExpensiveProduct)
                .cheapestProduct(cheapestProduct)
                .mostPopularCategory(mostPopularCategory)
                .lessPopularCategory(lessPopularCategory)
                .build();
    }

    public ProductInsightDTO getGlobalProductInsights(LocalDateTime start, LocalDateTime end) {

        // Producto global más vendido.
        ProductMetricDTO GlobalMostPopularProduct = orderDetailRepository.findGlobalMostPopularProducts(start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Producto global menos vendido.
        ProductMetricDTO GlobalLessPopularProduct = orderDetailRepository.findGlobalLessPopularProducts(start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Producto global mas caro.
        ProductMetricDTO GlobalMostExpensiveProduct = orderDetailRepository.findGlobalMostExpensiveProducts(start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Producto global más barato.
        ProductMetricDTO GlobalCheapestProduct = orderDetailRepository.findGlobalCheapestProducts(start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Categoria global mas vendida
        ProductCategoryMetricDTO GlobalMostPopularCategory = orderDetailRepository.findGlobalMostPopularCategories(start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        // Categoria menos vendida
        ProductCategoryMetricDTO GlobalLessPopularCategory = orderDetailRepository.findGlobalLessPopularCategories(start, end)
                .stream()
                .findFirst()    // sacamos solo el primero.
                .orElse(null);

        return ProductInsightDTO.builder() // Patron builder para construir el DTO del insight y devolverlo.
                .mostPopularProduct(GlobalMostPopularProduct)
                .lessPopularProduct(GlobalLessPopularProduct)
                .mostExpensiveProduct(GlobalMostExpensiveProduct)
                .cheapestProduct(GlobalCheapestProduct)
                .mostPopularCategory(GlobalMostPopularCategory)
                .lessPopularCategory(GlobalLessPopularCategory)
                .build();

    }




}
