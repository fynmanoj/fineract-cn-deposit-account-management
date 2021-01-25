package org.apache.fineract.cn.deposit.service.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubTransactionTypeRepository extends JpaRepository<SubTransactionTypeEntity, Long> {
    Optional<SubTransactionTypeEntity> findByIdentifier(final String identifier);
}
