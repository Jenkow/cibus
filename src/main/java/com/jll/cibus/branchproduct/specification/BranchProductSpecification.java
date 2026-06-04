package com.jll.cibus.branchproduct.specification;

import com.jll.cibus.branchproduct.entity.BranchProductEntity;
import com.jll.cibus.common.exception.BusinessException;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.productcategory.entity.ProductCategoryEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.math.BigDecimal;

public class BranchProductSpecification {

    private BranchProductSpecification() {
    }

    public static PredicateSpecification<BranchProductEntity> equalsBranchId(Long branchId) {
        return (root, cb) -> branchId == null || branchId <= 0
                ? cb.conjunction()
                : cb.equal(root.get("branch").get("id"), branchId);
    }

    public static PredicateSpecification<BranchProductEntity> equalsProductId(Long productId) {
        return (root, cb) -> productId == null || productId <= 0
                ? cb.conjunction()
                : cb.equal(root.get("product").get("id"), productId);
    }

    public static PredicateSpecification<BranchProductEntity> containsProductName(String productName) {
        return (root, cb) -> productName == null || productName.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%");
    }

    public static PredicateSpecification<BranchProductEntity> equalsCategoryId(Long categoryId) {
        return (root, cb) -> {
            if (categoryId == null || categoryId <= 0) return cb.conjunction();
            Join<BranchProductEntity, ProductEntity> product = root.join("product");
            Join<ProductEntity, ProductCategoryEntity> category = product.join("category");
            return cb.equal(category.get("id"), categoryId);
        };
    }

    public static PredicateSpecification<BranchProductEntity> isAvailable(Boolean available) {
        return (root, cb) -> available == null
                ? cb.conjunction()
                : cb.equal(root.get("available"), available);
    }

    public static PredicateSpecification<BranchProductEntity> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return (root, cb) -> minPrice == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static PredicateSpecification<BranchProductEntity> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        return (root, cb) -> maxPrice == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    //----------------------------------------------------------------------------------------------------------
    /*
    public static PredicateSpecification<BranchProductEntity> nameStartsWith(String name) {
        return (root, cb) -> name == null || name.isBlank()
                ? cb.conjunction() : cb.like(cb.lower(root.get("firstName")), name.toLowerCase());
    }

    public static PredicateSpecification<BranchProductEntity> equalsPrice(BigDecimal price) {
        return (root, cb) -> price == null || price.compareTo(BigDecimal.ZERO) <= 0
                ? cb.conjunction()
                : cb.equal(root.get("price"), price);
    }
    */
}
