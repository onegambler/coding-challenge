package com.n26.repository;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;

import com.n26.model.Transaction;
import com.n26.util.TransactionModelUtil;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class DelayedTransactionTest extends TransactionModelUtil {

    private static final int EXPIRATION_DELAY = 120;

    @Test
    public void getDelay_whenTransactionIsNotExpiredThenReturnLongGreaterOrEqualThanZero() {
        final Transaction transaction = getTransaction(TEN);
        final DelayedTransaction delayedTransaction = new DelayedTransaction(transaction, EXPIRATION_DELAY);
        final long delay = delayedTransaction.getDelay(TimeUnit.SECONDS);

        assertThat(delay).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void getDelay_whenTransactionIsExpiredThenReturnLongLessThanZero() {
        final ZonedDateTime dateTime5MinutesInThePast = ZonedDateTime.now().minusMinutes(5);
        final Transaction transaction = getTransaction(TEN, dateTime5MinutesInThePast);
        final DelayedTransaction delayedTransaction = new DelayedTransaction(transaction, EXPIRATION_DELAY);
        final long delay = delayedTransaction.getDelay(TimeUnit.SECONDS);

        assertThat(delay).isLessThan(0);
    }


    @Test
    public void compareTo_whenFirstHasASmallerTimeStampThenReturnANegativeInteger() {
        final Transaction first = getTransaction(TEN);
        final Transaction second = getTransaction(TEN, ZonedDateTime.now().plusSeconds(5));

        DelayedTransaction firstDelayed = new DelayedTransaction(first, 1);
        DelayedTransaction secondDelayed = new DelayedTransaction(second, 1);

        assertThat(firstDelayed.compareTo(secondDelayed)).isLessThan(0);
    }

    @Test
    public void compareTo_whenTransactionsHaveTheSamexsTimestampThenReturnZero() {
        final ZonedDateTime now = ZonedDateTime.now();
        final Transaction first = getTransaction(TEN, now);
        final Transaction second = getTransaction(TEN, now);

        DelayedTransaction firstDelayed = new DelayedTransaction(first, 1);
        DelayedTransaction secondDelayed = new DelayedTransaction(second, 1);

        assertThat(firstDelayed.compareTo(secondDelayed)).isZero();
    }

    @Test
    public void compareTo_whenCompareAgainstSameTransactionReturnsZero() {
        final Transaction first = getTransaction(TEN);

        DelayedTransaction firstDelayed = new DelayedTransaction(first, 1);


        assertThat(firstDelayed.compareTo(firstDelayed)).isZero();
    }
}