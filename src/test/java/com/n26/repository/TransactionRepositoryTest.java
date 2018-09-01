package com.n26.repository;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.util.TransactionModelUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class TransactionRepositoryTest extends TransactionModelUtil {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Captor
    private ArgumentCaptor<Statistics> statisticsCaptor;

    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        transactionRepository = new TransactionRepository(statisticsRepository);
    }

    @Test
    public void addTransaction_shouldAddATransactionAndUpdateDataCorrectly() {
        final Transaction first = getTransaction(TEN);
        final Transaction second = getTransaction(ONE);

        transactionRepository.addTransaction(first);
        transactionRepository.addTransaction(second);

        assertThat(transactionRepository.getOrderedTransactions()).containsOnly(first, second);
        final BigDecimal expectedSum = BigDecimal.valueOf(11);
        assertThat(transactionRepository.getSum()).isEqualTo(expectedSum);

        verify(statisticsRepository, times(2)).setCurrentStatistics(statisticsCaptor.capture());
        verifyNoMoreInteractions(statisticsRepository);

        final Statistics statistics = statisticsCaptor.getValue();
        assertThat(statistics.getAmountAverage()).isEqualTo(BigDecimal.valueOf(5.5).setScale(2, ROUND_HALF_UP));
        assertThat(statistics.getAmountMax()).isEqualTo(TEN);
        assertThat(statistics.getAmountMin()).isEqualTo(ONE);
        assertThat(statistics.getAmountSum()).isEqualTo(expectedSum);
        assertThat(statistics.getTransactionsCount()).isEqualTo(2);
    }

    @Test
    public void removeTransaction_shouldRemoveATransactionAndUpdateDataCorrectly() {
        final Transaction first = getTransaction(TEN);
        final Transaction second = getTransaction(ONE);
        transactionRepository.addTransaction(first);
        transactionRepository.addTransaction(second);

        transactionRepository.removeTransaction(first);

        assertThat(transactionRepository.getOrderedTransactions()).containsOnly(second);
        final BigDecimal expectedSum = BigDecimal.ONE;
        assertThat(transactionRepository.getSum()).isEqualTo(expectedSum);

        verify(statisticsRepository, times(3)).setCurrentStatistics(statisticsCaptor.capture());
        verifyNoMoreInteractions(statisticsRepository);

        final Statistics statistics = statisticsCaptor.getValue();
        assertThat(statistics.getAmountAverage()).isEqualTo(BigDecimal.ONE.setScale(2, ROUND_HALF_UP));
        assertThat(statistics.getAmountMax()).isEqualTo(ONE);
        assertThat(statistics.getAmountMin()).isEqualTo(ONE);
        assertThat(statistics.getAmountSum()).isEqualTo(expectedSum);
        assertThat(statistics.getTransactionsCount()).isEqualTo(1);
    }

    @Test
    public void removeTransaction_whenTransactionDoesNotExistThenThrowException() {
        final Transaction first = getTransaction(TEN);

        assertThatThrownBy(() -> transactionRepository.removeTransaction(first))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Transaction does not exist");

        verifyZeroInteractions(statisticsRepository);

    }


    @Test
    public void removeAllTransaction_removeAllTransactionsAndUpdateDataCorrectly() {
        final Transaction first = getTransaction(TEN);
        final Transaction second = getTransaction(ONE);
        transactionRepository.addTransaction(first);
        transactionRepository.addTransaction(second);

        transactionRepository.deleteAllTransactions();

        assertThat(transactionRepository.getOrderedTransactions()).isEmpty();

        assertThat(transactionRepository.getSum()).isEqualTo(ZERO);

        verify(statisticsRepository, times(2)).setCurrentStatistics(statisticsCaptor.capture());
        verify(statisticsRepository).resetStatistics();
        verifyNoMoreInteractions(statisticsRepository);
    }
}