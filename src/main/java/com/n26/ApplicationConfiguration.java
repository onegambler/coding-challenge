package com.n26;


import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.n26.deserialiser.BigDecimalSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

    public static final int TRANSACTION_EXPIRATION_SECONDS = 60;

    @Autowired
    public void configureJackson(ObjectMapper jackson2ObjectMapper) {
        jackson2ObjectMapper.setSerializationInclusion(NON_NULL);

        SimpleModule bigDecimalModule = new SimpleModule("bigDecial")
            .addSerializer(BigDecimal.class, new BigDecimalSerialiser());
        jackson2ObjectMapper.registerModule(bigDecimalModule);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService taskScheduler() {
        return Executors.newScheduledThreadPool(2);
    }

    public int getTransactionExpirationInSeconds() {
        return TRANSACTION_EXPIRATION_SECONDS;
    }
}
