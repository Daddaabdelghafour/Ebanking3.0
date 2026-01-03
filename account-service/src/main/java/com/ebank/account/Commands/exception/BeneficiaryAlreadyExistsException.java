// BeneficiaryAlreadyExistsException.java
package com.ebank.account.Commands.exception;

public class BeneficiaryAlreadyExistsException extends RuntimeException {
    public BeneficiaryAlreadyExistsException(String rib) {
        super("Beneficiary with RIB " + rib + " already exists");
    }
}