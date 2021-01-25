package org.apache.fineract.cn.deposit.service.internal.command;

import org.apache.fineract.cn.deposit.api.v1.instance.domain.SubTransactionType;

public class UpdateSubTxnTypeCommand {
    private final SubTransactionType subTransactionType;

    public UpdateSubTxnTypeCommand(final SubTransactionType subTransactionType) {
        super();
        this.subTransactionType = subTransactionType;
    }

    public SubTransactionType subTransactionType() {
        return this.subTransactionType;
    }
}
