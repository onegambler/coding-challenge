package com.n26.util;

import com.n26.model.Transaction;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TransactionModelUtil {

    protected Transaction getTransaction(BigDecimal amount) {
        return getTransaction(amount, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    protected Transaction getTransaction(BigDecimal amount, ZonedDateTime timestamp) {
        return Transaction.builder()
            .amount(amount)
            .timestamp(timestamp)
            .build();
    }

}
