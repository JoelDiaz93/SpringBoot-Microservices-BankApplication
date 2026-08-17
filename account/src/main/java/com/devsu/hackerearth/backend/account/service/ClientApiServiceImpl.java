package com.devsu.hackerearth.backend.account.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.devsu.hackerearth.backend.account.model.dto.ClientResponseDto;

@Service
public class ClientApiServiceImpl implements ClientApiService {

    private final RestTemplate restTemplate;

    @Value("${client.service.url}")
    private String clientServiceUrl;

    public ClientApiServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    @Override
    public CompletableFuture<String> getClientName(Long clientId) {
        ClientResponseDto client = restTemplate.getForObject(
                clientServiceUrl + "/api/clients/" + clientId,
                ClientResponseDto.class);

        if (client == null) {
            throw new IllegalArgumentException(
                    "Client not found with id: " + clientId);
        }

        return CompletableFuture.completedFuture(client.getName());
    }
}
