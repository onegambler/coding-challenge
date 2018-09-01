package com.n26.repository;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import com.n26.model.Statistics;
import org.junit.Test;

import java.math.BigDecimal;

public class StatisticsRepositoryTest {

    private StatisticsRepository repository = new StatisticsRepository();

    @Test
    public void shouldInitialiseStatisticsWhenCreated() {
        assertThat(repository.getCurrentStatistics()).isNotNull();
    }

    @Test
    public void shouldCorrectlyResetStatistics() {
        final Statistics statistics = Statistics.builder()
            .amountSum(BigDecimal.TEN)
            .amountMin(BigDecimal.TEN)
            .amountMax(BigDecimal.TEN)
            .amountAverage(BigDecimal.TEN)
            .transactionsCount(4L)
            .build();
        repository.setCurrentStatistics(statistics);

        repository.resetStatistics();

        final Statistics currentStatistics = repository.getCurrentStatistics();
        assertThat(currentStatistics.getAmountAverage()).isEqualTo(ZERO);
        assertThat(currentStatistics.getAmountMax()).isEqualTo(ZERO);
        assertThat(currentStatistics.getAmountMin()).isEqualTo(ZERO);
        assertThat(currentStatistics.getAmountSum()).isEqualTo(ZERO);
        assertThat(currentStatistics.getTransactionsCount()).isEqualTo(0);
    }
}