package com.kinandcarta.ecommerce.clients;

import com.kinandcarta.ecommerce.entities.OrdersAccount;
import com.kinandcarta.ecommerce.exceptions.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
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

    @Value("${commerce.clients.accounts.serviceName}") String accountsServiceName;

    @LoadBalanced
    WebClient.Builder webClientBuilder;

    public AccountServiceClient(final WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public OrdersAccount findByAccountIdRef(final String id) throws AccountNotFoundException {
        Objects.requireNonNull(id, "Account Id REF required to find account using client.");

        ReactiveDiscoveryClient reactiveDiscoveryClient =
                new SimpleReactiveDiscoveryClient(new SimpleReactiveDiscoveryProperties());
        Flux<ServiceInstance> serviceInstancesList = reactiveDiscoveryClient.getInstances(StringUtils.isBlank(accountsServiceName) ? "ACCOUNTS-SERVICE" : accountsServiceName);
        if (null == serviceInstancesList) {
            throw new AccountNotFoundException("No instances of Account Services located in Registry.");
        }

        serviceInstancesList.toStream().findFirst().ifPresent(element -> this.baseURL = element.getUri().toString());
        log.info("findByAccountIdRef from Eureka Accounts Service instance: baseURL = " + this.baseURL);
        Mono<OrdersAccount> accountFound =
                webClientBuilder.baseUrl(this.baseURL).build().get()
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
