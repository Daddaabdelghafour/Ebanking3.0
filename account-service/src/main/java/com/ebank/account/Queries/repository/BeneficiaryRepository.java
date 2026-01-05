// BeneficiaryRepository.java
package com.ebank.account.Queries.repository;

import com.ebank.account.Queries.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {
    
    @Query("SELECT b FROM Beneficiary b WHERE b.account.id = :accountId AND b.deletedAt IS NULL AND b.isActive = true")
    List<Beneficiary> findByAccountId(@Param("accountId") UUID accountId);
    
    @Query("SELECT b FROM Beneficiary b WHERE b.id = :id AND b.account.id = :accountId AND b.deletedAt IS NULL")
    Optional<Beneficiary> findByIdAndAccountId(@Param("id") UUID id, @Param("accountId") UUID accountId);
    
    @Query("SELECT b FROM Beneficiary b WHERE b.account.id = :accountId AND b.beneficiaryRib = :rib AND b.deletedAt IS NULL")
    Optional<Beneficiary> findByAccountIdAndRib(@Param("accountId") UUID accountId, @Param("rib") String rib);
}