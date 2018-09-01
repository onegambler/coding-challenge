package com.n26.deserialiser;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalSerialiserTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new BigDecimalSerialiser());
        objectMapper.registerModule(module);
    }

    @Test
    public void shouldCorrectlySerialiseBigDecimals() {
        assertThat(objectMapper.convertValue(ZERO, String.class)).isEqualTo("0.00");
        assertThat(objectMapper.convertValue(ONE, String.class)).isEqualTo("1.00");
        assertThat(objectMapper.convertValue(TEN, String.class)).isEqualTo("10.00");
        assertThat(objectMapper.convertValue(BigDecimal.valueOf(12.3282822), String.class)).isEqualTo("12.33");
        assertThat(objectMapper.convertValue(BigDecimal.valueOf(14.1), String.class)).isEqualTo("14.10");
        assertThat(objectMapper.convertValue(BigDecimal.valueOf(100_000_000), String.class)).isEqualTo("100000000.00");
    }

}