package com.n26.repository;

import com.n26.model.Statistics;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class StatisticsRepository {

    private volatile Statistics currentStatistics = new Statistics();

    public void resetStatistics() {
        currentStatistics = Statistics.builder().build();
    }
}
