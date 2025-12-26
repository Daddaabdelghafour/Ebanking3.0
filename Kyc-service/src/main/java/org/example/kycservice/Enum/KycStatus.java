package org.example.kycservice.Enum;

public enum KycStatus {
    PENDING,       // 🟡 Just submitted, waiting
    IN_PROGRESS,   // 🟡 Agent is reviewing
    VERIFIED,      // 🟢 Approved! Can use banking
    REJECTED,      // 🔴 Not approved
    EXPIRED,       // 🟠 Was verified, but expired
    SUSPENDED      // 🔴 Account frozen
 }
