// GetBeneficiaryByIdQuery.java
package com.ebank.account.Queries.query;

import java.util.UUID;

public record GetBeneficiaryByIdQuery(
        UUID beneficiaryId,
        UUID accountId
) {
}