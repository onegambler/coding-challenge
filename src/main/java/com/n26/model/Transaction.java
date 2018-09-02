package com.n26.model;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Comparable<Transaction> {

    @NotNull
    private BigDecimal amount;

    @NotNull
    @PastOrPresent
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime timestamp;


    @Override
    public int compareTo(Transaction other) {
        if (isNull(other)) {
            throw new IllegalArgumentException("Transaction to compare cannot be null");
        }
        final int amountComparison = this.amount.compareTo(other.amount);

        return amountComparison == 0 ? this.timestamp.compareTo(other.timestamp) : amountComparison;
    }
}
