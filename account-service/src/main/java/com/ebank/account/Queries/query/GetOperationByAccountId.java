package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetOperationByAccountId(
        UUID accountId,
        int page,
        int size
) {
}
