package com.v1no.LJL.payment_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v1no.LJL.payment_service.model.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(String orderId);

    Page<Payment> findByUserId(UUID userId, Pageable pageable);

    List<Payment> findByUserIdAndStatus(UUID userId, Payment.Status status);

    List<Payment> findByStatusAndCreatedAtBefore(Payment.Status status, LocalDateTime createdAt);

    long countByUserIdAndStatus(UUID userId, Payment.Status status);

    boolean existsByOrderId(String orderId);
}