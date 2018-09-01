package com.n26.model;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.n26.util.TransactionModelUtil;
import org.junit.Test;

import java.time.ZonedDateTime;

public class TransactionTest extends TransactionModelUtil {

    @Test
    public void compareTo_whenFirstTransactionHasSmallerAmountThanOtherThenReturnNegativeInteger() {
        final Transaction first = getTransaction(TEN);
        final Transaction second = getTransaction(ZERO);

        assertThat(first.compareTo(second)).isGreaterThan(0);
    }

    @Test
    public void compareTo_whenFirstTransactionHaBiggerAmountThanOtherThenReturnPositiveInteger() {
        final Transaction first = getTransaction(ZERO);
        final Transaction second = getTransaction(TEN);

        assertThat(first.compareTo(second)).isLessThan(0);
    }

    @Test
    public void compareTo_whenTransactionsHaveTheSameAmountThenReturnTheOneWithSmallerTimestamp() {
        final Transaction first = getTransaction(TEN);
        final Transaction second = getTransaction(TEN, ZonedDateTime.now().plusSeconds(5));

        assertThat(first.compareTo(second)).isLessThan(0);
    }

    @Test
    public void compareTo_whenTransactionsHaveTheSameAmountAndTimestampThenReturnZero() {
        final ZonedDateTime now = ZonedDateTime.now();
        final Transaction first = getTransaction(TEN, now);
        final Transaction second = getTransaction(TEN, now);

        assertThat(first.compareTo(second)).isZero();
    }

    @Test
    public void compareTo_whenCompareAgainstSameTransactionReturnsZero() {
        final Transaction first = getTransaction(TEN);

        assertThat(first.compareTo(first)).isZero();
    }

    @Test
    public void compareTo_whenOtherIsNullThenThrowsException() {
        final Transaction first = getTransaction(TEN);

        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Transaction to compare cannot be null");
    }
}