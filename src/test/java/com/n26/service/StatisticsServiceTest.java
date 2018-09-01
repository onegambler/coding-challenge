package com.n26.service;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.n26.repository.StatisticsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    private StatisticsService statisticsService;

    @Before
    public void setUp() {
        statisticsService = new StatisticsService(statisticsRepository);
    }

    @Test
    public void getStatistics_WorksAsExpected() {
        statisticsService.getStatistics();
        verify(statisticsRepository).getCurrentStatistics();
        verifyNoMoreInteractions(statisticsRepository);
    }
}