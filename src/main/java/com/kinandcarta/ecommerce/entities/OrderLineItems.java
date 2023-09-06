package com.kinandcarta.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;


@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Entity
@Slf4j
@Table(name = "order_line_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderLineItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Null
    private Long orderId;

    @NotNull
    private Long productId;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal price;

    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name="created_dt")
    private Instant createDateTime;

    @UpdateTimestamp
    @Column(name="updated_dt")
    private Instant updateDateTime;

    public BigDecimal computeTotalPrice(final BigDecimal price, final int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderLineItems that = (OrderLineItems) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
