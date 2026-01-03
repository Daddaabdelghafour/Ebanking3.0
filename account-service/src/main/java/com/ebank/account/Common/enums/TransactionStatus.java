// TransactionStatus.java
package com.ebank.account.Common.enums;

public enum TransactionStatus {
    PENDING,
    OTP_SENT,
    OTP_VERIFIED,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    EXPIRED
}