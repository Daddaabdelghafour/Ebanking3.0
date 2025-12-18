package com.ebank.gateway.resolver;

import com.ebank.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MutationResolver {

    private final GatewayService gatewayService;

    // TODO: Implement GraphQL mutations
    // Example:
    // public Account createAccount(AccountInput input) {
    //     return gatewayService.createAccount(input);
    // }
}

