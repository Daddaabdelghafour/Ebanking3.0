package com.ebank.account.Queries.controller;

import com.ebank.account.Queries.dto.NotificationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification-service.url}")
public interface NotificationRestClient {
    @PostMapping("/bank/mailing/send")
    void sendNotification(@RequestBody @Valid NotificationRequestDTO notification);
}
