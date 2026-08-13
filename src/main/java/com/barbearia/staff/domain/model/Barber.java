package com.barbearia.staff.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "barbers")
@Builder
@SoftDelete
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String email;

    @ElementCollection
    @CollectionTable(name = "tb_barber_products", joinColumns = @JoinColumn(name = "barber_id"))
    @Column(name = "product_id")
    private List<UUID> specialtyIds;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = new CommissionRate(commissionRate).value();
    }
}
