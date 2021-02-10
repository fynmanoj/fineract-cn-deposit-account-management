/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fineract.cn.deposit.service.internal.command.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.deposit.api.v1.EventConstants;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.Collection;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.CollectionResponse;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.IndvCollectionResponse;
import org.apache.fineract.cn.deposit.service.ServiceConstants;
import org.apache.fineract.cn.deposit.service.internal.command.CreateCollectionCommand;
import org.apache.fineract.cn.deposit.service.internal.mapper.CollectionsMapper;
import org.apache.fineract.cn.deposit.service.internal.mapper.SubTransactionTypeMapper;
import org.apache.fineract.cn.deposit.service.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.apache.fineract.cn.postgresql.util.LocalDateTimeConverter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Aggregate
public class CollectionAggregate {
    private final Logger logger;
    private final CollectionsRepository collectionsRepository;
    private final SelfExpiringTokenRepository selfExpiringTokenRepository;
    private final ProductInstanceRepository productInstanceRepository;
    private final SubTransactionTypeRepository subTransactionTypeRepository;
    private final IndividualCollectionsRepository individualCollectionsRepository;

    @Value("${config.tokenExpirydays}")
    private Integer tokenExpirydays;

    public CollectionAggregate(@Qualifier(ServiceConstants.LOGGER_NAME) Logger logger,
                               CollectionsRepository collectionsRepository,
                               SelfExpiringTokenRepository selfExpiringTokenRepository,
                               ProductInstanceRepository productInstanceRepository,
                               SubTransactionTypeRepository subTransactionTypeRepository,
                               IndividualCollectionsRepository individualCollectionsRepository) {
        this.logger = logger;
        this.collectionsRepository = collectionsRepository;
        this.selfExpiringTokenRepository = selfExpiringTokenRepository;
        this.productInstanceRepository = productInstanceRepository;
        this.subTransactionTypeRepository = subTransactionTypeRepository;
        this.individualCollectionsRepository = individualCollectionsRepository;
    }

    @CommandHandler
    @EventEmitter(selectorName = EventConstants.SELECTOR_NAME, selectorValue = EventConstants.POST_COLLECTION)
    @Transactional
    public CollectionResponse createCollection(final CreateCollectionCommand createCollectionCommand) {
        final Collection collection =  createCollectionCommand.getCollection();
        final CollectionsEntity collectionsEntity = CollectionsMapper.map(collection);
        final ProductInstanceEntity account = productInstanceRepository.findByAccountIdentifier(
                collection.getAccountId()).orElseThrow(()->
                ServiceException.notFound("Account with reference {0} not found.", collection.getAccountId()));
        collectionsEntity.setAccount(account);
        if(StringUtils.isNotBlank(collection.getSubTxnId())) {
            final SubTransactionTypeEntity subTxnType = subTransactionTypeRepository.findByIdentifier(collection.getSubTxnId())
                    .orElseThrow(() -> ServiceException.notFound("Sub transaction type {0} not found.", collection.getSubTxnId()));
            collectionsEntity.setSubTxnType(subTxnType);
        }

        Set<IndividualCollectionsEntity> individualCollectionsEntities = CollectionsMapper.mapIndividual(collection, collectionsEntity);
        collectionsEntity.setIndividualCollections(individualCollectionsEntities);
        for(IndividualCollectionsEntity individualCollectionsEntity : individualCollectionsEntities){
            individualCollectionsEntity.setReference(getUniqueReferenceForIndividualCollection());
        }
        collectionsEntity.setReference(getUniqueReferenceForCollection());
        this.collectionsRepository.save(collectionsEntity);

        final List<IndvCollectionResponse> indvCollectionResponses = new ArrayList<>();
        //generate OTP
        LocalDateTime tokenExpireBy = collectionsEntity.getCreatedOn().plusDays(tokenExpirydays);
        for(IndividualCollectionsEntity individualCollectionsEntity : individualCollectionsEntities){
            SelfExpiringTokensEntity selfExpiringTokensEntity = generateToken("INDV_COLLECTION",
                    individualCollectionsEntity.getReference(), tokenExpireBy);

            IndvCollectionResponse indvCollectionResponse = new IndvCollectionResponse();
            indvCollectionResponse.setIBAN(individualCollectionsEntity.getAccountExternalId());
            indvCollectionResponse.setReference(individualCollectionsEntity.getReference());
            indvCollectionResponse.setToken(selfExpiringTokensEntity.getToken());
            indvCollectionResponse.setTokenExpireBy(selfExpiringTokensEntity.getTokenExpiresBy());
            indvCollectionResponses.add(indvCollectionResponse);
        }
        SelfExpiringTokensEntity selfExpiringTokensEntity = generateToken("COLLECTION",
                collectionsEntity.getReference(), tokenExpireBy);
        CollectionResponse collectionResponse = new CollectionResponse();
        collectionResponse.setReference(collectionsEntity.getReference());
        collectionResponse.setToken(selfExpiringTokensEntity.getToken());
        collectionResponse.setTokenExpireBy(selfExpiringTokensEntity.getTokenExpiresBy());
        collectionResponse.setPayments(indvCollectionResponses);
        return collectionResponse;
    }

    private SelfExpiringTokensEntity generateToken(String type, String reference, LocalDateTime expireBy) {
        SelfExpiringTokensEntity selfExpiringTokensEntity = new SelfExpiringTokensEntity();
        selfExpiringTokensEntity.setToken(getUniqueToken());
        selfExpiringTokensEntity.setTokenExpiresBy(expireBy);
        selfExpiringTokensEntity.setStatus(SelfExpiringTokensEntity.STATUS.ACTIVE.name());
        selfExpiringTokensEntity.setEntityType(type);
        selfExpiringTokensEntity.setEntityReference(reference);
        selfExpiringTokenRepository.save(selfExpiringTokensEntity);
        return selfExpiringTokensEntity;
    }

    private String getUniqueReferenceForCollection() {
        UUID uuid=UUID.randomUUID();

        while(collectionsRepository.findByReference(uuid.toString()).isPresent()){
            uuid=UUID.randomUUID();
        }
        return  uuid.toString();
    }

    private String getUniqueReferenceForIndividualCollection() {
        UUID uuid=UUID.randomUUID();

        while(individualCollectionsRepository.findByReference(uuid.toString()).isPresent()){
            uuid=UUID.randomUUID();
        }
        return  uuid.toString();
    }

    private String getUniqueToken() {
        String token = generateRandomString();
        while (selfExpiringTokenRepository.findByTokenAndStatus(token, SelfExpiringTokensEntity.STATUS.ACTIVE.name()).isPresent()){
            token = getUniqueToken();
        }
        return  token;
    }

    public String generateRandomString() {
        final char[] buf = new char[6];
        final String num = "0126789345" ;
        final char[] symbols = num.toCharArray();
        final Random random= new SecureRandom();
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }


}
