package org.apache.fineract.cn.deposit.service.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByIdentifier(final String identifier);

    List<TransactionEntity> findByAccountId(final String accountId);

    List<TransactionEntity> findByAccountIdAndTransactionDateBetween(final String accountId, LocalDateTime fromDate,
                                                                     LocalDateTime toDate);
}

