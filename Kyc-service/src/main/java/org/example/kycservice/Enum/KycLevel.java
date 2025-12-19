package org.example.kycservice.Enum;

public enum KycLevel {
    LEVEL_0,  // 🆕 NEW! Default when user registers → $0 (can't transact)
    LEVEL_1,  // Basic (just ID) → $1,000/day
    LEVEL_2,  // Standard (ID + address) → $10,000/day
    LEVEL_3,  // Advanced (full docs) → $50,000/day
    LEVEL_4   // Premium (business) → Unlimited
}