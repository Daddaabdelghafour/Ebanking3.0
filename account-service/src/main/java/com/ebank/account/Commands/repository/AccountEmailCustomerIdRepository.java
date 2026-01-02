package com.ebank.account.Commands.repository;

import com.ebank.account.Queries.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountEmailCustomerIdRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.deletedAt IS NULL")
    Account findByEmail(@Param("email") String email);

    @Query("SELECT a FROM Account a WHERE a.customerId = :customerId AND a.deletedAt IS NULL")
    Account findByCustomerId(@Param("customerId") UUID customerId);
}