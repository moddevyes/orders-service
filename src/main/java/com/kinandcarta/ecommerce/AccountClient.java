package com.kinandcarta.ecommerce;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@NoArgsConstructor
@Slf4j
public class AccountClient {
    @LoadBalanced
    WebClient webClient;

    @Value("${commerce.registry.user}")
    private String eurekaRegistryUser;

    @Value("${commerce.registry.passwd}")
    private String eurekaRegistryPwd;

    @Value("${commerce.clients.accounts.baseUrl:http://localhost:8000}")
    private String baseURL;

    @Value("${commerce.clients.accounts.findbyidUrl:/accounts/{id}")
    private String accountsURL;

    public OrdersAccount findById(final Long id) {
        return null;
    }

    private Optional<Mono<OrdersAccount>> doFind(final Long accountIdToFind) {
        // REF: https://www.baeldung.com/spring-mocking-webclient
        // REF: (mocking spec), https://stackoverflow.com/questions/67091244/webclient-requestheadersurispec-the-method-in-the-type-is-not-applicable-for-t
        // REF: https://www.baeldung.com/webflux-webclient-parameters
        // REF: https://www.baeldung.com/java-string-from-mono
        // REF: https://tedblob.com/webflux-get-value-from-mono/
        // REF: https://tedblob.com/spring-webflux-mono-subscribe/
        // Verify model has a valid account

        Optional<Mono<OrdersAccount>> ordersAccountResponse = Optional.empty();

        try {
            webClient = WebClient.builder()
                    .baseUrl(baseURL)
                    .build();

                    Optional.of(webClient.get()
                            .uri(uriBuilder -> uriBuilder.path(accountsURL).build(accountIdToFind))
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(OrdersAccount.class));

        } catch (final Exception e) {
            throw new MissingAccountException("Account not found for ID ->" + accountIdToFind);
        }

        return ordersAccountResponse;
    }
}
