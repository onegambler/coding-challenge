package com.n26.service;

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
    private DelayQueue<DelayedTransaction> transactionsQueue = new DelayQueue<>();
    private final int transactionExpirationInSeconds;
    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, ApplicationConfiguration configuration) {
        this.transactionRepository = transactionRepository;
        this.transactionExpirationInSeconds = configuration.getTransactionExpirationInSeconds();
    }

    public void addTransaction(Transaction transaction) {
        DelayedTransaction delayedTransaction = new DelayedTransaction(transaction, transactionExpirationInSeconds);
        transactionsQueue.add(delayedTransaction);
        transactionRepository.addTransaction(transaction);
    }

    public void deleteAllTransactions() {
        transactionsQueue.clear();
        transactionRepository.deleteAllTransactions();
    }

    public boolean isOutsideStatisticsTimeWindow(Transaction transaction) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return now.minusSeconds(transactionExpirationInSeconds).isAfter(transaction.getTimestamp());
    }

    @Scheduled(fixedRate = 500)
    private void consumeExpiredTransactions() throws InterruptedException {
        final DelayedTransaction expiredTransaction = transactionsQueue.take();
        if (nonNull(expiredTransaction)) {
            transactionRepository.removeTransaction(expiredTransaction.getTransaction());
        }
    }
}
