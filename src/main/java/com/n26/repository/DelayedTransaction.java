package com.n26.repository;

import com.n26.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public class DelayedTransaction implements Delayed {

    private Transaction transaction;
    private int expirationDelay;

    @Override
    public long getDelay(TimeUnit unit) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return transaction.getTimestamp().plusSeconds(expirationDelay).compareTo(now);
    }

    @Override
    public int compareTo(Delayed obj) {
        final Transaction other = ((DelayedTransaction) obj).transaction;
        return this.transaction.getTimestamp().compareTo(other.getTimestamp());
    }
}
