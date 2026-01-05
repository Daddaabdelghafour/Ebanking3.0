// GetTransactionByIdQuery.java
package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetTransactionByIdQuery(
        UUID transactionId,
        UUID accountId
) {
}