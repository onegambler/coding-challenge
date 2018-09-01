package com.n26.service;

import static com.n26.ApplicationConfiguration.TRANSACTION_EXPIRATION_SECONDS;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.n26.ApplicationConfiguration;
import com.n26.model.Transaction;
import com.n26.repository.DelayedTransaction;
import com.n26.repository.TransactionRepository;
import com.n26.util.TransactionModelUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest extends TransactionModelUtil {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @Captor
    private ArgumentCaptor<DelayedTransaction> delayedTransactionArgumentCaptor;

    private TransactionService transactionService;

    @Before
    public void setUp() {
        when(applicationConfiguration.getTransactionExpirationInSeconds()).thenReturn(TRANSACTION_EXPIRATION_SECONDS);
        transactionService = new TransactionService(transactionRepository, applicationConfiguration);
    }

    @Test
    public void addTransaction_addATransactionToRepository() {
        final Transaction transaction = getTransaction(TEN);
        transactionService.addTransaction(transaction);

        verify(transactionRepository).addTransaction(transaction);
        verifyNoMoreInteractions(transactionRepository);

        assertThat(transactionService.getTransactionsQueue()).hasSize(1);

        final DelayedTransaction delayedTransaction = transactionService.getTransactionsQueue().peek();

        assertThat(delayedTransaction.getTransaction()).isEqualTo(transaction);
        assertThat(delayedTransaction.getExpirationDelay()).isEqualTo(TRANSACTION_EXPIRATION_SECONDS);
    }

    @Test
    public void deleteTransaction_deleteTransactionsFromRepository() {
        final Transaction transaction = getTransaction(TEN);
        transactionService.addTransaction(transaction);
        verify(transactionRepository).addTransaction(transaction);

        transactionService.deleteAllTransactions();

        verify(transactionRepository).deleteAllTransactions();
        verifyNoMoreInteractions(transactionRepository);

        assertThat(transactionService.getTransactionsQueue()).isEmpty();
    }

    @Test
    public void isOutsideStatisticsTimeWindow_whenTransactionTimestampIsLessThanTheExpirationWindowThenReturnFalse() {
        final Transaction transaction = getTransaction(TEN);

        final boolean withinTimeWindow = transactionService.isOutsideStatisticsTimeWindow(transaction);

        assertThat(withinTimeWindow).isFalse();
    }

    @Test
    public void isOutsideStatisticsTimeWindow_whenTransactionTimestampIsGreaterThanTheExpirationWindowThenReturnTrue() {
        final Transaction transaction = getTransaction(TEN, ZonedDateTime.now().minusMinutes(5));

        final boolean withinTimeWindow = transactionService.isOutsideStatisticsTimeWindow(transaction);

        assertThat(withinTimeWindow).isTrue();
    }
}