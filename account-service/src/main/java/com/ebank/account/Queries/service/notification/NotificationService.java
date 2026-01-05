package com.ebank.account.Queries.service.notification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface NotificationService {
    void sendAccountCreationNotification(String accountNumber, String email, LocalDateTime createdAt);
    void sendAccountDeletedNotification(String accountNumber, String email, LocalDateTime deletedAt);
    void sendAccountActivationNotification(String accountNumber, String email, LocalDateTime activatedAt);
    void sendAccountSuspensionNotification(String accountNumber, String email, LocalDateTime suspendedAt);
    void sendAccountCreditedNotification(String accountNumber, String email, BigDecimal amountCredited, LocalDateTime creditedAt);
    void sendAccountDebitedNotification(String accountNumber, String email, BigDecimal amountDebited, LocalDateTime debitedAt);
}
