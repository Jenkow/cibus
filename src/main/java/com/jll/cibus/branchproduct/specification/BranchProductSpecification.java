package com.jll.cibus.branchproduct.specification;

import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.productcategory.entity.ProductCategoryEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.PredicateSpecification;
import java.math.BigDecimal;

public class BranchProductSpecification{

    private BranchProductSpecification() {}

    public static PredicateSpecification<BranchProductEntity> nameContains (String name) {
        return (root, cb) -> name == null || name.isBlank()
                ? cb.conjunction() : cb.like(cb.lower(root.get("firstName")), name.toLowerCase());
    }

    public static PredicateSpecification<BranchProductEntity> nameStartsWith (String name) {
        return (root, cb) -> name == null || name.isBlank()
                ? cb.conjunction() : cb.like(cb.lower(root.get("firstName")), name.toLowerCase());
    }

    public static PredicateSpecification<BranchProductEntity> PriceGreaterThan (BigDecimal price){
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new BusinessException("Price must be greater or equal than zero");

        return (root, cb) -> cb.greaterThan(root.get("price"), price);
    }

    public static PredicateSpecification<BranchProductEntity> PriceLesserThan (BigDecimal price){
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new BusinessException("Price must be greater than zero");

        return (root, cb) -> cb.lessThan(root.get("price"), price);
    }

    public static PredicateSpecification<BranchProductEntity> isAvailable (Boolean available){
        return (root, cb) -> available == null
                ? cb.conjunction()
                : cb.equal(root.get("available"), available);
    }

    public static PredicateSpecification<BranchProductEntity> equalsCategory (Long categoryId){
        return (root, cb) -> {
            if (categoryId == null || categoryId <= 0) return cb.conjunction();

            Join<BranchProductEntity, ProductEntity> product
                    = root.join("product");

            Join<ProductEntity, ProductCategoryEntity> category
                    = product.join("category");

            return cb.equal(category.get("id"), categoryId);
        };
    }

    public static PredicateSpecification<BranchProductEntity> equalsPrice(BigDecimal price){
        return (root, cb) -> price == null || price.compareTo(BigDecimal.ZERO) <= 0
                ? cb.conjunction()
                : cb.equal(root.get("price"), price);
    }

    public static PredicateSpecification<BranchProductEntity> equalsBranch(Long branchId) {
        return (root, cb) ->
                branchId == null || branchId <= 0
                        ? cb.conjunction()
                        : cb.equal(
                        root.get("branch").get("id"),
                        branchId
                );
    }
}
