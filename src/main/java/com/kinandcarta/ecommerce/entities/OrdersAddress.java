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

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orders_address")
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdersAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 200, message = "Address must be between 2 and 200 characters")
    @Column(name = "address1", columnDefinition = "varchar(200) default ''", nullable = false)
    private String address1;

    @Size(max = 200, message = "Address must be between 2 and 200 characters")
    @Column(name="address2", columnDefinition = "varchar(200) default ''")
    private String address2;

    @NotNull
    @Size(min = 2, max = 200, message = "City must be between 2 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false)
    private String city;

    @NotNull
    @Size(max = 2, message = "State only allows an abbreviation of 2 characters")
    @Column(columnDefinition = "varchar(2) default ''", nullable = false)
    private String state;

    @Size(max = 200, message = "Province must be between 0 and 200 characters")
    @Column(columnDefinition = "varchar(200) null default ''")
    private String province;

    @Size(min = 5, max = 10, message = "Postal Code must be between 5 and 10 numbers")
    @Column(columnDefinition = "varchar(10) not null default ''", nullable = false)
    @NotNull private String postalCode;

    @Size(max = 200, message = "Country must be between 0 and 200 characters")
    @Column(columnDefinition = "varchar(100) not null default ''")
    @NotNull private String country;

    @CreationTimestamp
    @Column(name="created_dt")
    private Instant createDateTime;

    @UpdateTimestamp
    @Column(name="updated_dt")
    private Instant updateDateTime;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrdersAddress that = (OrdersAddress) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
