package com.kinandcarta.ecommerce;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrdersDiscoveryClientTest {
    private static final String ACCOUNTS_SERVICE_NAME = "ACCOUNTS-SERVICE";
    private static final boolean NOT_SECURE_SVC = false;
    @Mock
    DiscoveryClient discoveryClient;

    List<ServiceInstance> serviceInstancesList;

    @BeforeEach
    void setUp() {
        serviceInstancesList = new ArrayList<>();
        serviceInstancesList.add(
                new DefaultServiceInstance(
                        "eureka-0001", ACCOUNTS_SERVICE_NAME, "localhost", 8001, NOT_SECURE_SVC));
    }

    @Test
    void shouldConnectToEurekaServer_andFindAccountsService() {
        when(discoveryClient.getInstances(ACCOUNTS_SERVICE_NAME))
                .thenReturn(serviceInstancesList);
        List<ServiceInstance> eurekaResponse = discoveryClient.getInstances(ACCOUNTS_SERVICE_NAME);
        assertThat(eurekaResponse).isNotEmpty();
        ServiceInstance serviceInstance = eurekaResponse.get(0);
        assertThat(serviceInstance).isNotNull()
                .hasFieldOrPropertyWithValue("instanceId", "eureka-0001")
                .hasFieldOrPropertyWithValue("serviceId", ACCOUNTS_SERVICE_NAME)
                .hasFieldOrPropertyWithValue("host", "localhost")
                .hasFieldOrPropertyWithValue("port", 8001);
    }

    @Test
    void shouldCallEurekaAccountService_usingReactiveDiscoveryClient() {
        SimpleReactiveDiscoveryProperties properties = Mockito.mock(SimpleReactiveDiscoveryProperties.class);
        ReactiveDiscoveryClient reactiveDiscoveryClient = Mockito.mock(SimpleReactiveDiscoveryClient.class);
        when(reactiveDiscoveryClient.getInstances(ACCOUNTS_SERVICE_NAME))
                .thenReturn(Flux.fromStream(serviceInstancesList.stream()));
        Flux<ServiceInstance> serviceInstanceFluxResponse =
                reactiveDiscoveryClient.getInstances(ACCOUNTS_SERVICE_NAME);
        assertThat(serviceInstanceFluxResponse).isNotNull();
        log.info("FLUX response:");
        serviceInstanceFluxResponse.toStream().forEach(System.out::println);
    }
}
