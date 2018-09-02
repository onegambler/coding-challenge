package com.n26.controller;

import static com.n26.util.JsonUtil.asJsonString;
import static java.math.BigDecimal.TEN;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import com.n26.util.TransactionModelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;


@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest extends TransactionModelUtil {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void createTransaction_whenTransactionIsWithinTimeWindowAndValidThenReturnsCreated() throws Exception {
        final Transaction transaction = getTransaction(TEN);
        when(transactionService.isOutsideStatisticsTimeWindow(transaction)).thenReturn(false);

        this.mockMvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON).content(asJsonString(transaction)))
            .andExpect(status().isCreated());

        verify(transactionService).isOutsideStatisticsTimeWindow(transaction);
        verify(transactionService).addTransaction(transaction);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void createTransaction_whenTransactionIsNotWithinTimeWindowAndValidThenReturnsNoContent() throws Exception {
        final Transaction transaction = getTransaction(TEN);
        when(transactionService.isOutsideStatisticsTimeWindow(transaction)).thenReturn(true);

        this.mockMvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON).content(asJsonString(transaction)))
            .andExpect(status().isNoContent());

        verify(transactionService).isOutsideStatisticsTimeWindow(transaction);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void createTransaction_whenTransactionJsonIsMalformedThenReturnsBadRequest() throws Exception {

        this.mockMvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON).content("{wrong}"))
            .andExpect(status().isBadRequest());

        verifyZeroInteractions(transactionService);
    }

    @Test
    public void createTransaction_whenTransactionTimestampIsInTheFutureThenReturnUnprocessableEntity()
        throws Exception {

        final Transaction transaction = getTransaction(TEN, ZonedDateTime.now().plusDays(1));
        when(transactionService.isOutsideStatisticsTimeWindow(transaction)).thenReturn(true);

        this.mockMvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON).content(asJsonString(transaction)))
            .andExpect(status().isUnprocessableEntity());

        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void createTransaction_whenTransactionAmountIsNullThenReturnUnprocessableEntity() throws Exception {

        final Transaction transaction = getTransaction(null);
        when(transactionService.isOutsideStatisticsTimeWindow(transaction)).thenReturn(true);

        this.mockMvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON).content(asJsonString(transaction)))
            .andExpect(status().isUnprocessableEntity());

        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void createTransaction_whenTransactionTimestampIsNullThenReturnUnprocessableEntity() throws Exception {

        final Transaction transaction = getTransaction(TEN, null);
        when(transactionService.isOutsideStatisticsTimeWindow(transaction)).thenReturn(true);

        this.mockMvc.perform(post("/transactions")
            .contentType(APPLICATION_JSON).content(asJsonString(transaction)))
            .andExpect(status().isUnprocessableEntity());

        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void deleteAllTransactions_shouldCorrectlyDeleteAllTransactions() throws Exception {
        this.mockMvc.perform(delete("/transactions"))
            .andExpect(status().isNoContent());

        verify(transactionService).deleteAllTransactions();
        verifyNoMoreInteractions(transactionService);
    }
}