package com.n26.model;

import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class Statistics {

    @Builder.Default
    @JsonProperty("sum")
    private BigDecimal amountSum = ZERO;
    @Builder.Default
    @JsonProperty("avg")
    private BigDecimal amountAverage = ZERO;
    @Builder.Default
    @JsonProperty("max")
    private BigDecimal amountMax = ZERO;
    @Builder.Default
    @JsonProperty("min")
    private BigDecimal amountMin = ZERO;
    @Builder.Default
    @JsonProperty("count")
    private Long transactionsCount = 0L;
}