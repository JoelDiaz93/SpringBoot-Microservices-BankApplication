package com.devsu.hackerearth.backend.account.service;

import java.util.concurrent.CompletableFuture;

public interface ClientApiService {
    CompletableFuture<String> getClientName(Long clientId);
}
