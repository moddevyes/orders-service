package com.kinandcarta.ecommerce;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "orders_account")
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrdersAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 200, message = "First Name must be between 2 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 200, message = "Last Name must be between 2 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false)
    private String lastName;

    @NotNull
    @Email(message = "Invalid e-mail address.")
    @Size(min = 3, max = 200, message = "Email address must be between 3 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false, unique = true)
    private String emailAddress;

    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<OrdersAddress> addresses;

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
        OrdersAccount that = (OrdersAccount) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
