// BeneficiaryNotFoundException.java
package com.ebank.account.Queries.exception;

import java.util.UUID;

public class BeneficiaryNotFoundException extends RuntimeException {
    public BeneficiaryNotFoundException(UUID beneficiaryId) {
        super("Beneficiary not found with id: " + beneficiaryId);
    }
}