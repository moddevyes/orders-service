package com.kinandcarta.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShippingAddress {
    private Long id;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String province;
    private String postalCode;
    private String country;
}
