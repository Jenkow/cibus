package com.jll.cibus.branchproduct;

import com.jll.cibus.branch.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchProductRepository extends JpaRepository<BranchProductEntity, Long> {

    List<BranchProductEntity> findAllByAvailableTrue();

    List<BranchProductEntity> findAllByAvailableFalse();

    List<BranchProductEntity> findAllByBranch(BranchEntity branch);

    List<BranchProductEntity> findAllByBranch_Id(Long branchId);
}
