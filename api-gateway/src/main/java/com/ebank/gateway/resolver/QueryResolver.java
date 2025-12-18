package com.ebank.gateway.resolver;

import com.ebank.gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryResolver {

    private final GatewayService gatewayService;

    // TODO: Implement GraphQL queries
    // Example:
    // public Customer getCustomer(String id) {
    //     return gatewayService.getCustomer(id);
    // }
}

