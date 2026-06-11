package com.jll.cibus.table.repository;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.table.entity.TableEntity;
import com.jll.cibus.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long>, JpaSpecificationExecutor<TableEntity> {


    List<TableEntity> findByBranchId(Long branchId);

    Optional<TableEntity> findByBranch_IdAndNumber(Long branchId, Integer number);

    List<TableEntity> findByWaiter_id(Long waiterId);


    boolean existsById(Long id);

    Boolean existsByIdAndBranchId(Long tableId, Long branchId);

    boolean existsByBranchIdAndNumber(Long branchId, Integer number);

}
