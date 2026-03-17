package com.v1no.LJL.payment_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_user", columnList = "user_id"),
    @Index(name = "idx_payment_order", columnList = "order_id"),
    @Index(name = "idx_payment_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Column(name = "order_id", nullable = false, unique = true, length = 50)
    private String orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "vnp_transaction_no", length = 50)
    private String vnpTransactionNo;

    @Column(name = "vnp_txn_ref", length = 50)
    private String vnpTxnRef;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vnp_response", columnDefinition = "jsonb")
    private Map<String, String> vnpResponse;

    @Column(name = "payment_url", length = 1000)
    private String paymentUrl;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED
    }

    // Helper methods
    public void markAsSuccess(String vnpTransactionNo, Map<String, String> response) {
        this.status = Status.SUCCESS;
        this.vnpTransactionNo = vnpTransactionNo;
        this.vnpResponse = response;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = Status.FAILED;
    }

    public static String generateOrderId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "VIP_" + timestamp;
    }
}