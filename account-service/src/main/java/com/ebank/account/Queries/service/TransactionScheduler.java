// TransactionScheduler.java
package com.ebank.account.Queries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionScheduler {
    
    private final TransactionService transactionService;
    
    // Run every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void expireOldTransactions() {
        log.debug("Running scheduled task to expire old transactions");
        transactionService.expireOldTransactions();
    }
}