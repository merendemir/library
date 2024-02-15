package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.LendTransactionRequestDto;
import com.application.library.data.view.transaction.lend.LendTransactionAuthUserView;
import com.application.library.data.view.transaction.lend.LendTransactionView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.LendTransaction;
import com.application.library.service.LendTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LendTransactionControllerTest extends BaseRestControllerTest {
    @MockBean
    private LendTransactionService lendTransactionService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new LendTransactionController(lendTransactionService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testLendBook_whenLendBookCalledWithValidRequest_shouldReturnLendTransactionId() throws Exception {
        // given
        LendTransactionRequestDto requestDto = new LendTransactionRequestDto();
        LendTransaction testLendTransaction = getTestLendTransaction(true);

        when(lendTransactionService.lendBook(requestDto)).thenReturn(testLendTransaction);

        mockMvc.perform(post("/api/lend/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testLendTransaction.getId().toString())));
    }

    @Test
    void testLendBook_whenLendBookCalledWithInvalidRequest_shouldReturnHTTP400() throws Exception {
        // given
        LendTransactionRequestDto requestDto = new LendTransactionRequestDto();
        String errorMessage = MessageConstants.BOOK_NOT_AVAILABLE_FOR_LENDING;

        when(lendTransactionService.lendBook(requestDto)).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(post("/api/lend/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testLendBook_whenLendBookCalledAndUserHasAlreadyLend_shouldReturnHTTP409() throws Exception {
        // given
        LendTransactionRequestDto requestDto = new LendTransactionRequestDto();
        String errorMessage = MessageConstants.USER_HAS_ALREADY_LENT_A_BOOK;

        when(lendTransactionService.lendBook(requestDto)).thenThrow(new EntityAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/api/lend/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testReturnBook_whenReturnBookCalledWithValidId_shouldReturnLendTransactionId() throws Exception {
        // given
        LendTransaction testLendTransaction = getTestLendTransaction(true);

        when(lendTransactionService.returnBook(testLendTransaction.getId())).thenReturn(testLendTransaction);

        mockMvc.perform(put("/api/lend/transactions/{id}/return", testLendTransaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testLendTransaction.getId().toString())));
    }


    @Test
    void testReturnBook_whenReturnBookCalledWithAlreadyReturnedId_shouldReturnHTTP400() throws Exception {
        // given
        LendTransaction testLendTransaction = getTestLendTransaction(false);
        String errorMessage = MessageConstants.BOOK_HAS_ALREADY_BEEN_RETURN;

        when(lendTransactionService.returnBook(testLendTransaction.getId())).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(put("/api/lend/transactions/{id}/return", testLendTransaction.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testReturnBook_whenReturnBookCalledWithMustPayLateFee_shouldReturnHTTP400() throws Exception {
        // given
        LendTransaction testLendTransaction = getTestLendTransaction(true);
        String errorMessage = MessageConstants.MUST_PAY_LATE_FEE;

        when(lendTransactionService.returnBook(testLendTransaction.getId())).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(put("/api/lend/transactions/{id}/return", testLendTransaction.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testPayLateFee_whenPayLateFeeCalledWithValidId_shouldReturnLendTransactionId() throws Exception {
        // given
        LendTransaction testLendTransaction = getTestLendTransaction(true);

        when(lendTransactionService.payLateFee(testLendTransaction.getId())).thenReturn(testLendTransaction);

        mockMvc.perform(put("/api/lend/transactions/{id}/pay/late/fee", testLendTransaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testLendTransaction.getId().toString())));
    }

    @Test
    void testPayLateFee_whenPayLateFeeCalledWithNoLateFee_shouldReturnHTTP400() throws Exception {
        // given
        LendTransaction testLendTransaction = getTestLendTransaction(true);
        String errorMessage = MessageConstants.NO_LATE_FEE_TO_PAY;

        when(lendTransactionService.payLateFee(testLendTransaction.getId())).thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(put("/api/lend/transactions/{id}/pay/late/fee", testLendTransaction.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetPayLateFeeById_whenGetPayLateFeeByIdCalledWithValidId_shouldReturnLateFee() throws Exception {
        // given
        LendTransaction testLendTransaction = getTestLendTransaction(true);

        when(lendTransactionService.getLateFeeById(testLendTransaction.getId())).thenReturn(10.0);

        mockMvc.perform(get("/api/lend/transactions/{id}/pay/late/fee", testLendTransaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(10.0)));
    }

    @Test
    void testFindLendTransactionsForAuthUser_whenFindLendTransactionsForAuthUserCalled_shouldReturnLendTransactions() throws Exception {
        // given
        int page = 0;
        int size = 10;

        List<LendTransactionAuthUserView> lendTransactionAuthUserView = List.of(getLendTransactionAuthUserView(false));
        PageImpl<LendTransactionAuthUserView> lendTransactionAuthUserViewPage = new PageImpl<>(lendTransactionAuthUserView, PageRequest.of(page, size), lendTransactionAuthUserView.size());

        when(lendTransactionService.findLendTransactionsForAuthUser(page, size)).thenReturn(lendTransactionAuthUserViewPage);

        mockMvc.perform(get("/api/lend/transactions/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(lendTransactionAuthUserView.get(0).getId().toString())));
    }

    @Test
    void testFindLendTransactionsByUserId_whenFindLendTransactionsByUserIdCalled_shouldReturnLendTransactions() throws Exception {
        // given
        int page = 0;
        int size = 10;
        Long userId = 1L;

        List<LendTransactionAuthUserView> lendTransactionAuthUserView = List.of(getLendTransactionAuthUserView(false));
        PageImpl<LendTransactionAuthUserView> lendTransactionAuthUserViewPage = new PageImpl<>(lendTransactionAuthUserView, PageRequest.of(page, size), lendTransactionAuthUserView.size());

        when(lendTransactionService.findLendTransactionsByUserId(userId, page, size)).thenReturn(lendTransactionAuthUserViewPage);

        mockMvc.perform(get("/api/lend/transactions/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(lendTransactionAuthUserView.get(0).getId().toString())));
    }

    @Test
    void testFindLendTransactionsByReturned_whenFindLendTransactionsByReturnedCalled_shouldReturnLendTransactions() throws Exception {
        // given
        int page = 0;
        int size = 10;
        boolean returned = false;

        List<LendTransactionView> lendTransactionView = List.of(getLendTransactionView(false));
        PageImpl<LendTransactionView> lendTransactionViewPage = new PageImpl<>(lendTransactionView, PageRequest.of(page, size), lendTransactionView.size());

        when(lendTransactionService.findLendTransactionsByReturned(returned, page, size)).thenReturn(lendTransactionViewPage);

        mockMvc.perform(get("/api/lend/transactions/returned/{returned}", returned)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(lendTransactionView.get(0).getId().toString())))
                .andExpect(jsonPath("$.data.content[0].returned", is(returned)));
    }
}