package com.ebank.account.Queries.service.notification;

import com.ebank.account.Queries.dto.NotificationRequestDTO;
import com.ebank.account.Queries.controller.NotificationRestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationServiceImpl implements NotificationService{
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm");
    private final NotificationRestClient notificationRestClient;

    public NotificationServiceImpl(NotificationRestClient notificationRestClient) {
        this.notificationRestClient = notificationRestClient;
    }

    @Async
    @Override
    public void sendAccountCreationNotification(
            String accountNumber,
            String email,
            LocalDateTime createdAt
    ){
        String formattedDate = formatDateTime(createdAt);
        String body = NotificationTemplate
                .ACCOUNT_CREATION
                .formatBody(formattedDate, accountNumber);
        sendNotification(
                email,
                NotificationTemplate
                        .ACCOUNT_CREATION
                        .getTitle(),
                NotificationTemplate
                        .ACCOUNT_CREATION
                        .getBody());
    }

    @Async
    @Override
    public void sendAccountDeletedNotification(
            String accountNumber,
            String email,
            LocalDateTime deletedAt
    ) {
        String formattedDate = formatDateTime(deletedAt);
        String body = NotificationTemplate
                .ACCOUNT_DELETED
                .formatBody(accountNumber, formattedDate);
        sendNotification(
                email,
                NotificationTemplate
                        .ACCOUNT_DELETED
                        .getTitle(),
                NotificationTemplate
                        .ACCOUNT_DELETED
                        .getBody());
    }

    @Async
    @Override
    public void sendAccountActivationNotification(
            String accountNumber,
            String email,
            LocalDateTime activatedAt
    ) {
        String formattedDate = formatDateTime(activatedAt);
        String body = NotificationTemplate
                .ACCOUNT_ACTIVATION
                .formatBody(formattedDate);
        sendNotification(
                email,
                NotificationTemplate
                        .ACCOUNT_ACTIVATION
                        .getTitle(),
                NotificationTemplate
                        .ACCOUNT_ACTIVATION
                        .getBody());
    }

    @Async
    @Override
    public void sendAccountSuspensionNotification(
            String accountNumber,
            String email,
            LocalDateTime suspendedAt
    ) {
        String formattedDate = formatDateTime(suspendedAt);
        String body = NotificationTemplate
                .ACCOUNT_SUSPENSION
                .formatBody(formattedDate);
        sendNotification(
                email,
                NotificationTemplate
                        .ACCOUNT_SUSPENSION
                        .getTitle(),
                NotificationTemplate
                        .ACCOUNT_SUSPENSION
                        .getBody());
    }

    @Async
    @Override
    public void sendAccountCreditedNotification(String accountNumber, String email, BigDecimal amountCredited, LocalDateTime creditedAt) {
        String formattedDate = formatDateTime(creditedAt);
        String body = NotificationTemplate
                .ACCOUNT_CREDITED
                .formatBody(
                        accountNumber,
                        amountCredited,
                        formattedDate
                );
        sendNotification(
                email,
                NotificationTemplate.ACCOUNT_CREDITED.getTitle(),
                NotificationTemplate.ACCOUNT_CREDITED.getBody()
        );
    }

    @Async
    @Override
    public void sendAccountDebitedNotification(String accountNumber, String email, BigDecimal amountDebited, LocalDateTime debitedAt) {
        String formattedDate = formatDateTime(debitedAt);
        String body = NotificationTemplate
                .ACCOUNT_DEBITED
                .formatBody(
                        accountNumber,
                        amountDebited,
                        formattedDate
                );
        sendNotification(
                email,
                NotificationTemplate
                        .ACCOUNT_DEBITED
                        .getTitle(),
                NotificationTemplate
                        .ACCOUNT_DEBITED
                        .getBody()
                );
    }

    private void sendNotification(String email, String title, String body) {
        try {
            NotificationRequestDTO notification = new NotificationRequestDTO(email, title, body);
            notificationRestClient.sendNotification(notification);
        } catch (Exception e) {

        }
    }

    @NotNull
    private String formatDateTime(@NotNull LocalDateTime localDateTime){
        return localDateTime.format(dateTimeFormatter);
    }
}
