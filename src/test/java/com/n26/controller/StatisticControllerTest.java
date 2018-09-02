package com.n26.controller;

import static com.n26.util.JsonUtil.asJsonString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n26.model.Statistics;
import com.n26.service.StatisticsService;
import com.n26.util.TransactionModelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@RunWith(SpringRunner.class)
@WebMvcTest(StatisticController.class)
public class StatisticControllerTest extends TransactionModelUtil {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    public void createTransaction_whenTransactionIsWithinTimeWindowAndValidThenReturnsCreated() throws Exception {
        Statistics statistics = Statistics.builder().build();

        when(statisticsService.getStatistics()).thenReturn(statistics);

        final MvcResult mvcResult = this.mockMvc.perform(get("/statistics"))
            .andExpect(status().isOk())
            .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        JSONAssert.assertEquals(actual, asJsonString(statistics), true);

        verify(statisticsService).getStatistics();
        verifyNoMoreInteractions(statisticsService);
    }
}