package com.jll.cibus.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {

    Optional<ProductCategoryEntity> findByNameIgnoreCase(String name);

}
