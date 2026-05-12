package com.jll.cibus.table;

import com.jll.cibus.branch.BranchEntity;
import com.jll.cibus.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long>
{
    List<TableEntity> findByBranch (BranchEntity branch);
    List<TableEntity> findByBranchId (Long branchId);
    List<TableEntity> findByBranchAndAvailable (BranchEntity branch, Boolean available);
    List<TableEntity> findByBranchIdAndAvailable (Long branchId, Boolean available);
    List<TableEntity> findByBranchAndWaiter (BranchEntity branch, UserEntity waiter);
    List<TableEntity> findByBranchIdAndWaiterId (Long branchId, Long userId);
}
