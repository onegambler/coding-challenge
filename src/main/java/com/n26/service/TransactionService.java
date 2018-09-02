package com.n26.service;

import static com.n26.ApplicationConfiguration.CONSUMER_SCHEDULER_TIME_IN_MS;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PACKAGE;

import com.n26.ApplicationConfiguration;
import com.n26.model.Transaction;
import com.n26.repository.DelayedTransaction;
import com.n26.repository.TransactionRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.DelayQueue;

@Service
public class TransactionService {

    @Getter(PACKAGE)
    private DelayQueue<DelayedTransaction> transactionsToExpireAfterDelay = new DelayQueue<>();
    private final int transactionExpirationInSeconds;
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, ApplicationConfiguration configuration) {
        this.transactionRepository = transactionRepository;
        this.transactionExpirationInSeconds = configuration.getTransactionExpirationInSeconds();
    }

    public void addTransaction(Transaction transaction) {
        DelayedTransaction transactionToExpireAfterDelay =
            new DelayedTransaction(transaction, transactionExpirationInSeconds);
        transactionsToExpireAfterDelay.add(transactionToExpireAfterDelay);
        transactionRepository.addTransaction(transaction);
    }

    public void deleteAllTransactions() {
        transactionsToExpireAfterDelay.clear();
        transactionRepository.deleteAllTransactions();
    }

    public boolean isOutsideStatisticsTimeWindow(Transaction transaction) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return now.minusSeconds(transactionExpirationInSeconds).isAfter(transaction.getTimestamp());
    }

    /**
     * Since there is no information about the number of requests that can arrive/expire per seconds, I set a
     * specific value for the scheduler `fixedRate` = 100ms and number of threads = 5. These numbers are
     * sufficient to have all integration tests pass. It should be able to handle roughly 40~50 expiring transactions
     * per second.
     *
     * If required by the non functional requirements these numbers can be tweaked to handle a bigger load.
     *
     */
    @Scheduled(fixedRate = CONSUMER_SCHEDULER_TIME_IN_MS)
    private void consumeExpiredTransactions() throws InterruptedException {
        final DelayedTransaction expiredTransaction = transactionsToExpireAfterDelay.take();
        if (nonNull(expiredTransaction)) {
            transactionRepository.removeTransaction(expiredTransaction.getTransaction());
        }
    }
}
