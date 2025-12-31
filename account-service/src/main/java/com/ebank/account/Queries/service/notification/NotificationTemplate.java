package com.ebank.account.Queries.service.notification;

import lombok.Getter;

@Getter
public enum NotificationTemplate {
    ACCOUNT_CREATION(
            "Account created",
            """
                    Dear Customer,
                    
                    Your account has been successfully created.
                    
                    Your bank account number is %s.
                    
                    Thank you for choosing our bank.
                    
                    Best regards,
                    E-Bank Team"""
    ),
    ACCOUNT_DELETED(
            "Account deleted",
            """
                    Dear Customer,
                    
                    We regret to inform you that your account with account number %s has been deleted as per your request.
                    
                    If you have any questions or need further assistance, please do not hesitate to contact our customer support team.
                    
                    Thank you for banking with us.
                    
                    Best regards,
                    E-Bank Team"""
    ),
    ACCOUNT_ACTIVATION(
            "Account activated",
            """
                    Dear Customer,
                    
                    We are pleased to inform you that your account with account number %s has been successfully activated.
                    
                    You can now access all the features and services associated with your account.
                    
                    Thank you for choosing our bank.
                    
                    Best regards,
                    E-Bank Team"""
    ),
    ACCOUNT_SUSPENSION(
            "Account suspended",
            """
                    Dear Customer,
                    
                    We would like to inform you that your account with account number %s has been temporarily suspended due to suspicious activities.
                    
                    Please contact our customer support team immediately to resolve this issue and restore access to your account.
                    
                    Thank you for your prompt attention to this matter.
                    
                    Best regards,
                    E-Bank Team"""
    ),
    ACCOUNT_CREDITED(
            "Account credited",
            """
                    Dear Customer,
                    
                    We are pleased to inform you that your account with account number %s has been credited with an amount of %s %s.
                    
                    Thank you for banking with us.
                    
                    Best regards,
                    E-Bank Team"""
    ),
    ACCOUNT_DEBITED(
            "Account debited",
            """
                    Dear Customer,
                    
                    We would like to inform you that your account with account number %s has been debited with an amount of %s %s.
                    
                    Best regards,
                    E-Bank Team"""
    );


    private final String title;
    private final String body;

    NotificationTemplate(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String formatBody(Object... args) {
        return String.format(this.body, args);
    }
}
