package com.kinandcarta.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orders")
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    public OrdersAccount ordersAccount;

    @Size(min = 10, max = 255, message = "Order number must be between 10 and 255 characters")
    @Column(unique = false)
    @NotNull
    private String orderNumber;

    @NotNull
    private Instant orderDate;

    @OneToOne(cascade = CascadeType.ALL)
    private OrdersAddress ordersShippingAddress;

    @Column(columnDefinition = "decimal(38, 2) null default 0")
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name="created_dt")
    private Instant createDateTime;

    @UpdateTimestamp
    @Column(name="updated_dt")
    private Instant updateDateTime;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderLineItems> orderLineItems;

    public BigDecimal sumLineItems(final Set<OrderLineItems> lineItems) {
        Set<OrderLineItems> items =
                new HashSet<>(lineItems.isEmpty() ? new HashSet<>() : lineItems);

        Optional<BigDecimal> total =
                Optional.of(items
                        .stream()
                        .map(this::computeTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));


        this.setTotalPrice(total.orElse(BigDecimal.ZERO));
        return total.orElse(BigDecimal.ZERO);
    }

    public BigDecimal computeTotalPrice(final OrderLineItems lineItem) {
        BigDecimal totalLineItemPrice = lineItem.getPrice().multiply(BigDecimal.valueOf(lineItem.getQuantity()));
        lineItem.setTotalPrice(totalLineItemPrice);
        return totalLineItemPrice;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Orders orders = (Orders) o;
        return getId() != null && Objects.equals(getId(), orders.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
