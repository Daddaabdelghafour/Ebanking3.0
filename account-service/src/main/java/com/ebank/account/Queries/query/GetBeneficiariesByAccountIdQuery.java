// GetBeneficiariesByAccountIdQuery.java
package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetBeneficiariesByAccountIdQuery(
        UUID accountId
) {
}