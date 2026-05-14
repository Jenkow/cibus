package com.jll.cibus.order;

import com.jll.cibus.order.repository.OrderRepository;
import com.jll.cibus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private UserRepository userRepository;



}
