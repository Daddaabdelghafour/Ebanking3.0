package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetOperationsByAccountId(
        UUID accountId,
        int page,
        int size
) {
}
