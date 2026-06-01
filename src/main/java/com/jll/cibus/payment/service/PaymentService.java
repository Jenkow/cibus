package com.jll.cibus.payment.service;

import com.jll.cibus.common.exception.ResourceAlreadyExistsException;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.payment.dto.PaymentRequestDTO;
import com.jll.cibus.payment.dto.PaymentResponseDTO;
import com.jll.cibus.payment.entity.PaymentEntity;
import com.jll.cibus.payment.mapper.PaymentMapper;
import com.jll.cibus.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentResponseDTO> findAll(){
        List<PaymentEntity> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toDTO)
                .toList();
    }

    public PaymentEntity getEntity(Long id){
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("id",id));
    }

    public PaymentEntity getEntityByName(String name){
        return paymentRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("name",name));
    }

    public PaymentResponseDTO findById(Long id){
        PaymentEntity payment = getEntity(id);
        return paymentMapper.toDTO(payment);
    }

    public List<PaymentResponseDTO> findByName(String name){
        List<PaymentEntity> payments = paymentRepository.findByNameContaining(name);
        return payments.stream()
                .map(paymentMapper::toDTO)
                .toList();
    }

    public void existsByName(String name){
        if(paymentRepository.existsByName(name)){
            throw new ResourceAlreadyExistsException("name", name);
        }
    }

    @Transactional
    public PaymentResponseDTO create(PaymentRequestDTO newPayment){
        existsByName(newPayment.getName());
        PaymentEntity payment = paymentMapper.toEntity(newPayment);
        PaymentEntity saved = paymentRepository.save(payment);
        return paymentMapper.toDTO(saved);
    }

    @Transactional
    public PaymentResponseDTO update(Long id, PaymentRequestDTO newPayment){
        existsByName(newPayment.getName());
        PaymentEntity payment = getEntity(id);
        payment.setName(newPayment.getName());
        PaymentEntity saved = paymentRepository.save(payment);
        return paymentMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long id){
        PaymentEntity payment = getEntity(id);
        paymentRepository.delete(payment);
    }

}
