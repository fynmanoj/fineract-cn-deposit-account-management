package org.apache.fineract.cn.deposit.service.internal.repository;

import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "shed_collections_inidividual")
public class IndividualCollectionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "collections_id", nullable = false)
    private CollectionsEntity collection;

    @ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_identifier", nullable = false)
    private ProductInstanceEntity account;

    @Column(name = "account_external_id")
    private String accountExternalId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "i_reference")
    private String reference;

    public IndividualCollectionsEntity() {
        super();
    }

    public Long getId() {
        return id;
    }

    public CollectionsEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionsEntity collection) {
        this.collection = collection;
    }

    public ProductInstanceEntity getAccount() {
        return account;
    }

    public void setAccount(ProductInstanceEntity account) {
        this.account = account;
    }

    public String getAccountExternalId() {
        return accountExternalId;
    }

    public void setAccountExternalId(String accountExternalId) {
        this.accountExternalId = accountExternalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
