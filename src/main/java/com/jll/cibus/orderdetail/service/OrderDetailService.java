package com.jll.cibus.orderdetail.service;

import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.orderdetail.dto.OrderDetailRequestDTO;
import com.jll.cibus.orderdetail.dto.OrderDetailResponseDTO;
import com.jll.cibus.orderdetail.entity.OrderDetailEntity;
import com.jll.cibus.orderdetail.mapper.OrderDetailMapper;
import com.jll.cibus.orderdetail.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService
{
    private OrderDetailEntity orderDetailEntity;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public OrderDetailResponseDTO createOrderDetail (OrderDetailRequestDTO dto)
    {
         OrderDetailEntity orderDetail = orderDetailMapper.toEntity(dto);
         OrderDetailEntity savedOrderDetail = orderDetailRepository.save(orderDetail);

         return  orderDetailMapper.toDTO(savedOrderDetail);
    }
    public List<OrderDetailResponseDTO> findAll()
    {
        List<OrderDetailEntity> orderDetails = orderDetailRepository.findAll();
            return orderDetails.stream()
                    .map(orderDetailMapper::toDTO)
                    .toList();
    }
    private OrderDetailEntity getOrderDetailEntityById (Long id)
    {
        return orderDetailRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("order detail", id));
    }
    public OrderDetailResponseDTO findById (Long id)
    {
        OrderDetailEntity orderDetail= getOrderDetailEntityById(id);
        return orderDetailMapper.toDTO(orderDetail);
    }
    //public OrderDetailResponseDTO findByOrderId (Long orderId)
    //findByOrderIdAndProductId
    //update

}
