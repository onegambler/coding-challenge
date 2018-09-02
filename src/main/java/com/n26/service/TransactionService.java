package com.n26.service;

import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PACKAGE;

import com.n26.ApplicationConfiguration;
import com.n26.model.Transaction;
import com.n26.repository.DelayedTransaction;
import com.n26.repository.TransactionRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class TransactionService {

    @Getter(PACKAGE)
    private DelayQueue<DelayedTransaction> transactionsToExpireAfterDelay = new DelayQueue<>();
    private final int transactionExpirationInSeconds;
    private ThreadPoolExecutor consumers;
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              ApplicationConfiguration configuration,
                              ThreadPoolExecutor consumers) {
        this.transactionRepository = transactionRepository;
        this.transactionExpirationInSeconds = configuration.getTransactionExpirationInSeconds();
        this.consumers = consumers;
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
     * specific value the number of threads = 5. Sufficient to have all integration tests pass.
     * <p>
     * If required by the non functional requirements threads can be added to handle a bigger load.
     */
    @PostConstruct
    @SuppressWarnings("InfiniteLoopStatement")
    private void startConsumers() {
        for (int i = 0; i < consumers.getCorePoolSize(); i++) {
            consumers.execute(() -> {
                while (true) {
                    consumeExpireTransaction();
                }
            });
        }
    }

    private void consumeExpireTransaction() {
        final DelayedTransaction expiredTransaction;
        try {
            expiredTransaction = transactionsToExpireAfterDelay.take();
            if (nonNull(expiredTransaction)) {
                transactionRepository.removeTransaction(expiredTransaction.getTransaction());
            }
        } catch (InterruptedException e) {
            log.error("Interrupted consumer", e);
        }
    }
}
