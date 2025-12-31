package com.ebank.account.Queries.repository;

import com.ebank.account.Queries.entity.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {

    @Query( " SELECT o " +
            " FROM Operation o " +
            " WHERE o.account.id= :accountId " +
            " AND o.deletedAt IS NULL " +
            " ORDER BY o.createdAt DESC ")
    Page<Operation> findByAccountIdAndDeletedAtIsNull(UUID accountId, Pageable pageable);

    @Query( " SELECT o " +
            " FROM Operation o " +
            " WHERE o.account.id= :accountId " +
            " ORDER BY o.createdAt DESC ")
    Page<Operation> findByAccountId(@Param("accountId") UUID accountId, Pageable pageable);

    @Query( " SELECT o " +
            " FROM Operation o " +
            " WHERE o.account.id = :accountId ")
    List<Operation> findAllByAccountId(@Param("accountId") UUID accountId);
}
