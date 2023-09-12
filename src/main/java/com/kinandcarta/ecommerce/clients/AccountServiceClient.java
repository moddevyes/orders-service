package com.kinandcarta.ecommerce.clients;

import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.exceptions.AccountNotFoundException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

public class AccountServiceClient {
    String baseURL;
    String getAccountIdUri;
    WebClient client;


    public AccountServiceClient(final String baseURL, final String getAccountIdUri) {
        this.client = WebClient.builder().baseUrl(baseURL).build();
        this.baseURL = baseURL;
        this.getAccountIdUri = getAccountIdUri;
    }

    public OrdersAccount findByAccountIdRef(final String id) throws AccountNotFoundException {
        Objects.requireNonNull(id, "Account Id REF required to find account using client.");

        Mono<OrdersAccount> accountFound =
                client.get().uri(this.getAccountIdUri + id)
                    .retrieve()
                    .bodyToMono(OrdersAccount.class);

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
