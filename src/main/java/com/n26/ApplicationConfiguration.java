package com.n26;


import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.n26.deserialiser.BigDecimalSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.math.BigDecimal;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

    private static final int CONSUMER_SCHEDULER_THREADS_NUM = 5;

    public static final int TRANSACTION_EXPIRATION_SECONDS = 60;

    @Autowired
    public void configureJackson(ObjectMapper jackson2ObjectMapper) {
        jackson2ObjectMapper.setSerializationInclusion(NON_NULL);

        SimpleModule bigDecimalModule = new SimpleModule("bigDecimal")
            .addSerializer(BigDecimal.class, new BigDecimalSerialiser());
        jackson2ObjectMapper.registerModule(bigDecimalModule);
    }

    public int getTransactionExpirationInSeconds() {
        return TRANSACTION_EXPIRATION_SECONDS;
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(CONSUMER_SCHEDULER_THREADS_NUM);
        threadPoolTaskScheduler.setThreadNamePrefix("threadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    public ThreadPoolExecutor consumers() {
        return new ThreadPoolExecutor(CONSUMER_SCHEDULER_THREADS_NUM, CONSUMER_SCHEDULER_THREADS_NUM,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }
}
