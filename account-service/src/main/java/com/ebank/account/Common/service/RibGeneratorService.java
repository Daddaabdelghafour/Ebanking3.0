package com.ebank.account.Common.service;

import com.ebank.account.Common.configurations.BankProperties;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class RibGeneratorService {
    private final BankProperties bankProperties;
    private final EntityManager entityManager;

    /**
     * Generates 16-digit unique identifier for a bank account (account number).
     */
    @Transactional
    public String generateAccountNumber() {
        BigInteger nextVal = (BigInteger) entityManager
                .createNativeQuery("SELECT NEXTVAL('account_number_seq')", BigInteger.class)
                .getSingleResult();

        long accountNumberInitialValue = Long.parseLong(bankProperties.getAccountNumberInitialValue());
        long accountNumber = accountNumberInitialValue + nextVal.longValue() - 1;

        return String.format("%016d", accountNumber);
    }

    /**
     * Calculate the 2-digit RIB key using modulo 97 algorithm.
     * Formula: 97 - (first 22 digits % 97)
     */
    public String calculateRibKey(String accountNumber) {
        String first22Digits = bankProperties.getBankCode()
                + bankProperties.getCityCode()
                + accountNumber;
        long number = Long.parseLong(first22Digits);
        int ribKey = 97 - (int) (number % 97);

        return String.format("%02d", ribKey);
    }

    /**
     * Calculate complete IBAN with ckeck digits.
     * Format: MA +  check digits + bank code + city code + account number + RIB key
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
     */
    private int modulo97(String number) {
        int remainder = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            remainder = (remainder * 10 + digit) % 97;
        }
        return remainder;
    }
}