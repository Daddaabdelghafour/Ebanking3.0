package com.ebank.user.repo;

import com.ebank.user.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, UUID> {
    Customer findByUserId(UUID userId);

    @Query("SELECT c FROM Customer c WHERE c.cinOrPassport = :cinOrPassport")
    Customer findByCinOrPassportValue(@Param("cinOrPassport") String cinOrPassport);

    Customer findByEmail(String email);

}