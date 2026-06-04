package com.jll.cibus.table.repository;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {


    List<TableEntity> findByBranchId(Long branchId);

    Optional<TableEntity> findByBranch_IdAndNumber(Long branchId, Integer number);


    boolean existsById(Long id);

    Boolean existsByIdAndBranchId(Long tableId, Long branchId);

    boolean existsByBranchIdAndNumber(Long branchId, Integer number);


    /* No se usan pero estaban (implementar o borrar):

    List<TableEntity> findByBranch(BranchEntity branch);

    List<TableEntity> findByBranchAndAvailable(BranchEntity branch, Boolean available);

    List<TableEntity> findByBranchIdAndAvailable(Long branchId, Boolean available);

    List<TableEntity> findByBranchAndWaiter(BranchEntity branch, UserEntity waiter);

    List<TableEntity> findByBranchIdAndWaiterId(Long branchId, Long userId);
     */
}
