package com.barbearia.financial.domain.model;

import com.barbearia.shared.domain.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "barber_commissions")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Commission {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "barber_id", nullable = false) private UUID barberId;
    @Column(name = "barber_name", nullable = false) private String barberName;
    @Column(name = "appointment_id", nullable = false) private UUID appointmentId;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal serviceAmount;
    @Column(nullable = false, precision = 5, scale = 2) private BigDecimal commissionRate;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal commissionAmount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CommissionStatus status;
    @CreationTimestamp private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public void pay() {
        if (status == CommissionStatus.PAID) throw new BusinessException("Commission is already paid");
        status = CommissionStatus.PAID;
        paidAt = LocalDateTime.now();
    }
}
