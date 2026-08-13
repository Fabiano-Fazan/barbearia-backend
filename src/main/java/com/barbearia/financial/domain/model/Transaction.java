package com.barbearia.financial.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "financial_transactions")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder @SoftDelete
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(length = 500) private String description;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TransactionType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TransactionCategory category;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentMethod paymentMethod;
    @Column(name = "appointment_id") private UUID appointmentId;
    @Column(name = "barber_id") private UUID barberId;
    @Column(name = "barber_name") private String barberName;
    @CreationTimestamp private LocalDateTime transactionDate;
    private LocalDateTime paidDate;
}
