/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.deposit.api.v1.collection.domain;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Collection {
    @NotNull
    private LocalDateTime txnDate;
    @NotNull
    private BigDecimal amount;

    private BigDecimal transportFeeAmount;
    @NotNull
    private String currency;

    private String remarks;

    @NotNull
    private String accountId;

    private String subTxnId;

    private List<IndividualCollection> payment;

    public Collection() {
    }

    public LocalDateTime getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDateTime txnDate) {
        this.txnDate = txnDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTransportFeeAmount() {
        return transportFeeAmount;
    }

    public void setTransportFeeAmount(BigDecimal transportFeeAmount) {
        this.transportFeeAmount = transportFeeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSubTxnId() {
        return subTxnId;
    }

    public void setSubTxnId(String subTxnId) {
        this.subTxnId = subTxnId;
    }

    public List<IndividualCollection> getPayment() {
        return payment;
    }

    public void setPayment(List<IndividualCollection> payment) {
        this.payment = payment;
    }
}
