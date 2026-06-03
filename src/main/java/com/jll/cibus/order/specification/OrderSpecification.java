package com.jll.cibus.order.specification;

import com.jll.cibus.order.entity.OrderEntity;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    private OrderSpecification() {}

    public static PredicateSpecification<OrderEntity> equalsBranchId(Long branchId) {
        return (root, cb) -> {
            if (branchId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("branch").get("id"), branchId);
        };
    }

    public static PredicateSpecification<OrderEntity> equalsTableId(Long tableId) {
        return (root, cb) -> {
            if (tableId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("table").get("id"), tableId);
        };
    }

    public static PredicateSpecification<OrderEntity> equalsWaiterId(Long waiterId) {
        return (root, cb) -> {
            if (waiterId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("waiter").get("id"), waiterId);
        };
    }

    public static PredicateSpecification<OrderEntity> equalsStatus(String statusName){
        return (root, cb) -> {
          if(statusName == null || statusName.isBlank()){
              return cb.conjunction();
          }
          return cb.equal(cb.lower(root.get("status").get("name")), statusName.toLowerCase());
        };
    }

    public static PredicateSpecification <OrderEntity> equalsPaid(Boolean paid){
        return(root, cb) -> {
          if(paid == null){
              return cb.conjunction();
          }
          return cb.equal(root.get("paid"), paid);
        };
    }

    public static PredicateSpecification<OrderEntity> dateTimeAfter(LocalDateTime from){
        return(root, cb) -> {
            if(from == null){
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("dateTime"), from);
        };
    }

    public static PredicateSpecification<OrderEntity> dateTimeBefore(LocalDateTime to){
        return (root, cb) -> {
            if (to == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("dateTime"), to);
        };
    }

    public static PredicateSpecification<OrderEntity> totalGreaterThanOrEqual(BigDecimal minTotal){
        return (root, cb) -> {
            if (minTotal == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("total"), minTotal);
        };
    }

    public static PredicateSpecification<OrderEntity> totalLessThanOrEqual(BigDecimal maxTotal){
        return (root, cb) -> {
            if (maxTotal == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("total"), maxTotal);
        };
    }

}
