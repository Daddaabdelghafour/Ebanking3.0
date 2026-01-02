package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetOperationByIdQuery(
        UUID operationId
) {
}
