package com.kinandcarta.ecommerce.clients;

import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.exceptions.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class AccountServiceClient {
    @Value("${commerce.clients.accounts.baseUrl}") String baseURL;

    @Value("${commerce.clients.accounts.findByAccountIdRefUrl}") String getAccountIdUri;

    public OrdersAccount findByAccountIdRef(final String id) throws AccountNotFoundException {
        Objects.requireNonNull(id, "Account Id REF required to find account using client.");
        log.info("findByAccountIdRef, baseURL " + this.baseURL + ", path " + this.getAccountIdUri + ", id " + id);
        WebClient client = WebClient.create(this.baseURL);
        Mono<OrdersAccount> accountFound =
                client.get()
                        .uri(uriBuilder -> uriBuilder.path(this.getAccountIdUri).build(id))
                        .accept(MediaType.APPLICATION_JSON)
                        .acceptCharset(StandardCharsets.UTF_8)
                    .retrieve()
                    .bodyToMono(OrdersAccount.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));

        if (accountFound.map(this::toOrdersAccount).blockOptional().isEmpty()) {
            throw new AccountNotFoundException("Account not found for accountIdRef: " + id);
        }

        Optional<OrdersAccount> ordersAccount =
                accountFound.map(this::toOrdersAccount)
                .blockOptional();

        if (ordersAccount.isPresent() && !id.equalsIgnoreCase(ordersAccount.get().getAccountRefId())) {
            throw new AccountNotFoundException("Account not found for accountIdRef: " + id);
        }

        return ordersAccount.orElse(null);
    }

    private OrdersAccount toOrdersAccount(final OrdersAccount account) {    
        Objects.requireNonNull(account, "AccountServiceClient, null Account");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> First name");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> Last name");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> Email address");
        Objects.requireNonNull(account, "AccountServiceClient, null Account-> Addresses");
        
        if (account.getAddresses().isEmpty()) {
            throw new IllegalArgumentException("AccountServiceClient, at least ONE address is required");
        }

        return OrdersAccount.builder()
            .id(account.getId())
            .accountRefId(account.getAccountRefId())
            .firstName(account.getFirstName())
            .lastName(account.getLastName())
            .emailAddress(account.getEmailAddress())
            .addresses(account.getAddresses()).build();
    }
}
