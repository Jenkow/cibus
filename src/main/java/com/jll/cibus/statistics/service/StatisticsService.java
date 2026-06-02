package com.jll.cibus.statistics.service;

import com.jll.cibus.branchproduct.service.BranchProductService;
import com.jll.cibus.order.service.OrderService;
import com.jll.cibus.orderdetail.service.OrderDetailService;
import com.jll.cibus.payment.service.PaymentService;
import com.jll.cibus.table.service.TableService;
import com.jll.cibus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StatisticsService {

    //-------- PARA APROVECHAR AL 100% ESTA FEATURE ES NECESARIO TENER LOGGERS DE TODOS LOS SERVICIOS.

    private final TableService tableService; // metricas mesa mas concurrida por ej.
    private final BranchProductService branchProductService; // metricas producto mas caro a mas barato
    private final OrderService orderService; // metricas de
    private final OrderDetailService orderDetailService; // metricas de
    private final UserService userService; // metricas de mozos,
    private final PaymentService paymentService; // metricas de pago mas frecuentado
}
