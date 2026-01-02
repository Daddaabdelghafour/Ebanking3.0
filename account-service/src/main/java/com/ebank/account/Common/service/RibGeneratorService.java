package com.ebank.account.Common.service;

import com.ebank.account.Common.configurations.BankProperties;
import com.ebank.account.Queries.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RibGeneratorService {
    private final BankProperties bankProperties;
    private final AccountRepository accountRepository;

    /**
     * Generates 16-digit unique identifier for a bank account (account number).
     * Uses the maximum existing account number + 1, or initial value if none exist.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized String generateAccountNumber() {
        try {
            String maxAccountNumber = accountRepository.findMaxAccountNumber();

            long nextAccountNumber;
            if (maxAccountNumber == null || maxAccountNumber.isEmpty()) {
                // No accounts exist yet, use initial value
                nextAccountNumber = Long.parseLong(bankProperties.getAccountNumberInitialValue());
                log.info("First account being created. Using initial value: {}", nextAccountNumber);
            } else {
                // Increment the max account number
                nextAccountNumber = Long.parseLong(maxAccountNumber) + 1;
                log.info("Generating new account number: {} (previous max: {})", nextAccountNumber, maxAccountNumber);
            }

            String accountNumber = String.format("%016d", nextAccountNumber);
            log.debug("Generated account number: {}", accountNumber);
            return accountNumber;

        } catch (Exception e) {
            log.error("Error generating account number", e);
            throw new RuntimeException("Failed to generate account number: " + e.getMessage(), e);
        }
    }

    /**
     * Calculate the 2-digit RIB key using modulo 97 algorithm.
     * Formula: 97 - (first 22 digits % 97)
     * Uses BigInteger to handle large numbers.
     */
    public String calculateRibKey(String accountNumber) {
        String first22Digits = bankProperties.getBankCode()
                + bankProperties.getCityCode()
                + accountNumber;

        // Use BigInteger for large number calculation
        BigInteger number = new BigInteger(first22Digits);
        BigInteger modulo = number.mod(BigInteger.valueOf(97));
        int ribKey = 97 - modulo.intValue();

        return String.format("%02d", ribKey);
    }

    /**
     * Calculate complete IBAN with check digits.
     * Format: MA + check digits + bank code + city code + account number + RIB key
     */
    public String calculateIban(String accountNumber, String ribKey) {
        String rib = bankProperties.getBankCode()
                + bankProperties.getCityCode()
                + accountNumber
                + ribKey;

        // Rearrange: RIB + MA00, then convert letters to numbers
        String rearranged = rib + "22100"; // M=22 A=10

        // Calculate check digits using 98 - modulo 97
        int checkDigits = 98 - modulo97(rearranged);

        return "MA" + String.format("%02d", checkDigits) + rib;
    }

    /**
     * Get formatted RIB (24 digits).
     */
    public String getFormattedRib(String accountNumber, String ribKey) {
        return bankProperties.getBankCode()
                + bankProperties.getCityCode()
                + accountNumber
                + ribKey;
    }

    /**
     * Helper method to calculate modulo 97 for large numbers represented as strings.
     * Uses BigInteger to handle numbers larger than Long.MAX_VALUE.
     */
    private int modulo97(String number) {
        BigInteger bigNumber = new BigInteger(number);
        BigInteger remainder = bigNumber.mod(BigInteger.valueOf(97));
        return remainder.intValue();
    }
}