package com.jll.cibus.branchproduct;

import com.jll.cibus.branch.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchProductRepository extends JpaRepository<BranchProductEntity, Long> {

    List<BranchProductEntity> findAllByBranch_Name(String name);

    List<BranchProductEntity> findAllByBranch_Id(Long branchId);

    List<BranchProductEntity> findAllByBranch_IdAndAvailableTrue(Long branchId);

    Optional<BranchProductEntity> findByBranch_IdAndProduct_Id(Long branchId, Long productId);

    boolean existsByBranch_IdAndProduct_Id(Long branchId, Long productId);

}
