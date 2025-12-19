package com.ebank.user.dto;

import java.util.UUID;

public record CustomerCreatedEvent(
        UUID userId
) {}
