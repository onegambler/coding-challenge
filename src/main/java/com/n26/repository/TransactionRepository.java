package com.n26.repository;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.util.WithLock;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.TreeSet;

@Getter
@Repository
public class TransactionRepository extends WithLock {

    private TreeSet<Transaction> orderedTransactions = new TreeSet<>();
    private BigDecimal sum = ZERO;

    private StatisticsRepository statisticsRepository;

    @Autowired
    public TransactionRepository(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public void addTransaction(Transaction transaction) {
        withLock(() -> {
            orderedTransactions.add(transaction);

            final BigDecimal transactionAmount = transaction.getAmount();
            sum = sum.add(transactionAmount);
            updateStatistics();
        });

    }

    public void removeTransaction(Transaction transaction) {
        withLock(() -> {
            final boolean removed = orderedTransactions.remove(transaction);
            if (!removed) {
                throw new IllegalStateException("Transaction does not exist");
            }
            sum = sum.subtract(transaction.getAmount());
            updateStatistics();
        });
    }

    public void deleteAllTransactions() {
        withLock(() -> {
            orderedTransactions.clear();
            sum = ZERO;
            statisticsRepository.resetStatistics();
        });
    }

    private void updateStatistics() {
        final long transactionsCount = orderedTransactions.size();
        Statistics newStatistics = Statistics.builder()
            .transactionsCount(transactionsCount)
            .amountAverage(sum.setScale(2, ROUND_HALF_UP).divide(BigDecimal.valueOf(transactionsCount), ROUND_HALF_UP))
            .amountMax(orderedTransactions.last().getAmount())
            .amountMin(orderedTransactions.first().getAmount())
            .amountSum(sum)
            .build();

        statisticsRepository.setCurrentStatistics(newStatistics);
    }
}
