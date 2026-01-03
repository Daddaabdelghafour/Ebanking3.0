// GetTransactionsByAccountIdQuery.java
package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetTransactionsByAccountIdQuery(
        UUID accountId,
        int page,
        int size
) {
}