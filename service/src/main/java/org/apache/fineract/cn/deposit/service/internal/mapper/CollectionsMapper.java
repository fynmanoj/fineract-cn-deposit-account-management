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

package org.apache.fineract.cn.deposit.service.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.Collection;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.CollectionStatus;
import org.apache.fineract.cn.deposit.api.v1.collection.domain.IndividualCollection;
import org.apache.fineract.cn.deposit.service.internal.repository.CollectionsEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.IndividualCollectionsEntity;
import org.apache.fineract.cn.deposit.service.internal.repository.ProductInstanceEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class CollectionsMapper {
    public CollectionsMapper() {
    }

    public static CollectionsEntity map(Collection collection){
        CollectionsEntity collectionsEntity = new CollectionsEntity();
        collectionsEntity.setTransactionDate(collection.getTxnDate());
        collectionsEntity.setAmount(collection.getAmount());
        collectionsEntity.setTransportFeeAmount(collection.getTransportFeeAmount());
        collectionsEntity.setCurrency(collection.getCurrency());
        collectionsEntity.setRemarks(collection.getRemarks());
        collectionsEntity.setStatus(CollectionStatus.PENDING.name());
        collectionsEntity.setCreatedBy(getLoginUser());
        collectionsEntity.setCreatedOn(getNow());
        /*txn.setLastModifiedBy();
        txn.setLastModifiedOn();*/
        return collectionsEntity;
    }

    private static String getLoginUser() {
        return UserContextHolder.checkedGetUser();
    }

    private static LocalDateTime getNow() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    public static Set<IndividualCollectionsEntity> mapIndividual(Collection collection, CollectionsEntity collectionsEntity) {
        Set<IndividualCollectionsEntity> individualCollectionsEntities = new LinkedHashSet<>();
        for(IndividualCollection individualCollection : collection.getPayment()) {
            IndividualCollectionsEntity individualCollectionsEntity = new IndividualCollectionsEntity();
            individualCollectionsEntity.setCollection(collectionsEntity);
            individualCollectionsEntity.setAccountExternalId(individualCollection.getIBAN());
            individualCollectionsEntity.setAmount(individualCollection.getAmount());
            individualCollectionsEntities.add(individualCollectionsEntity);
        }
        return individualCollectionsEntities;
    }
}
