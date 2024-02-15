package com.application.library.service;

import com.application.library.converter.LendTransactionConverter;
import com.application.library.data.dto.LendTransactionRequestDto;
import com.application.library.data.view.transaction.lend.LendTransactionAuthUserView;
import com.application.library.data.view.transaction.lend.LendTransactionView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.LendTransaction;
import com.application.library.model.User;
import com.application.library.repository.LendTransactionRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LendTransactionServiceTest extends TestSupport {

    private LendTransactionRepository lendTransactionRepository;
    private ApplicationEventPublisher applicationEventPublisher;
    private LendTransactionConverter lendTransactionConverter;
    private SettingsService settingsService;
    private LendTransactionService lendTransactionService;

    private static MockedStatic<AuthHelper> authHelperMockedStatic;

    @BeforeAll
    static void beforeAll() {
        authHelperMockedStatic = mockStatic(AuthHelper.class);
    }

    @AfterAll
    static void afterAll() {
        authHelperMockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        lendTransactionRepository = mock(LendTransactionRepository.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        lendTransactionConverter = mock(LendTransactionConverter.class);
        settingsService = mock(SettingsService.class);

        lendTransactionService = new LendTransactionService(lendTransactionRepository, applicationEventPublisher, lendTransactionConverter, settingsService);
    }

    @Test
    void testFindById_whenFindByIdCalledWithExistsId_shouldReturnLendTransaction() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = new LendTransaction();
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));

        // when
        LendTransaction result = lendTransactionService.findById(id);

        // then
        assertEquals(lendTransaction, result);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_whenFindByIdCalledWithNotExistsId_shouldThrowEntityNotFoundException() {
        // given
        UUID id = UUID.randomUUID();

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lendTransactionService.findById(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testLendBook_whenLendBookCalledWithRequestDto_shouldReturnLendTransaction() {
        // given
        LendTransactionRequestDto requestDto = new LendTransactionRequestDto();
        LendTransaction lendTransaction = getTestLendTransaction(false);

        // when
        when(lendTransactionConverter.toEntity(requestDto)).thenReturn(lendTransaction);
        when(lendTransactionRepository.save(lendTransaction)).thenReturn(lendTransaction);

        // then
        LendTransaction result = lendTransactionService.lendBook(requestDto);
        assertEquals(lendTransaction, result);

        verify(lendTransactionRepository, times(1)).save(any());
        verify(applicationEventPublisher, times(2)).publishEvent(any());
    }

    @Test
    void testReturnBook_whenReturnBookCalledWithExistsIdAndNotReturned_shouldReturnLendTransaction() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction beforeLend = getTestLendTransaction(false);
        beforeLend.setDeadlineDate(LocalDate.now());

        LendTransaction afterLend = getTestLendTransaction(beforeLend.getId(), true);
        afterLend.setReturnDate(LocalDateTime.now());
        afterLend.setDeadlineDate(LocalDate.now());

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(beforeLend));
        when(settingsService.getLateFeePerDay()).thenReturn(10.0);

        // then
        LendTransaction result = lendTransactionService.returnBook(id);
        assertEquals(afterLend, result);

        verify(lendTransactionRepository, times(1)).findById(id);
        verify(applicationEventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void testReturnBook_whenReturnBookCalledWithExistsIdAndReturned_shouldThrowIllegalStateException() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = getTestLendTransaction(true);
        lendTransaction.setReturned(true);

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));

        // then
        assertThatThrownBy(() -> lendTransactionService.returnBook(id))
                .isInstanceOf(IllegalStateException.class);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testReturnBook_whenReturnBookCalledWithExistsIdAndNotReturnedAndLateFee_shouldThrowIllegalStateException() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = getTestLendTransaction(false);
        lendTransaction.setDeadlineDate(LocalDate.now().minusDays(1));

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));
        when(settingsService.getLateFeePerDay()).thenReturn(10.0);

        // then
        assertThatThrownBy(() -> lendTransactionService.returnBook(id))
                .isInstanceOf(IllegalStateException.class);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testPayLateFee_whenPayLateFeeCalledWithExistsIdAndLateFee_shouldReturnLendTransaction() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = getTestLendTransaction(false);
        lendTransaction.setDeadlineDate(LocalDate.now().minusDays(2));
        lendTransaction.setLateFeePaid(10.0);

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));
        when(settingsService.getLateFeePerDay()).thenReturn(10.0);

        // then
        LendTransaction result = lendTransactionService.payLateFee(id);
        assertEquals(lendTransaction, result);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testPayLateFee_whenPayLateFeeCalledWithExistsIdAndNoLateFee_shouldThrowIllegalStateException() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = getTestLendTransaction(false);
        lendTransaction.setDeadlineDate(LocalDate.now().plusDays(2));

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));
        when(settingsService.getLateFeePerDay()).thenReturn(10.0);

        // then
        assertThatThrownBy(() -> lendTransactionService.payLateFee(id))
                .isInstanceOf(IllegalStateException.class);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testFindLendTransactionsForAuthUser_whenFindLendTransactionsForAuthUserCalled_shouldReturnPageOfLendTransactionAuthUserView() {
        // given
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        LendTransactionAuthUserView testLendTransactionView = getLendTransactionAuthUserView(false);
        User testUser = getTestUser();

        Page<LendTransactionAuthUserView> expectedResult = new PageImpl<>(List.of(testLendTransactionView));

        // when
        when(AuthHelper.getActiveUser()).thenReturn(testUser);
        when(lendTransactionRepository.findAllByUser_Id(testUser.getId(), pageRequest)).thenReturn(expectedResult);

        // then
        Page<LendTransactionAuthUserView> response = lendTransactionService.findLendTransactionsForAuthUser(page, size);
        assertEquals(expectedResult, response);

        verify(lendTransactionRepository, times(1)).findAllByUser_Id(testUser.getId(), pageRequest);
    }

    @Test
    void testFindLendTransactionsByUserId_whenFindLendTransactionsByUserIdCalled_shouldReturnPageOfLendTransactionAuthUserView() {
        // given
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        LendTransactionAuthUserView testLendTransactionView = getLendTransactionAuthUserView(false);
        Long userId = 1L;

        Page<LendTransactionAuthUserView> expectedResult = new PageImpl<>(List.of(testLendTransactionView));

        // when
        when(lendTransactionRepository.findAllByUser_Id(userId, pageRequest)).thenReturn(expectedResult);

        // then
        Page<LendTransactionAuthUserView> response = lendTransactionService.findLendTransactionsByUserId(userId, page, size);
        assertEquals(expectedResult, response);

        verify(lendTransactionRepository, times(1)).findAllByUser_Id(userId, pageRequest);
    }

    @Test
    void testFindLendTransactionsByReturned_whenFindLendTransactionsByReturnedCalled_shouldReturnPageOfLendTransactionView() {
        // given
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        LendTransactionView testLendTransactionView = getLendTransactionView(false);
        boolean returned = false;

        Page<LendTransactionView> expectedResult = new PageImpl<>(List.of(testLendTransactionView));

        // when
        when(lendTransactionRepository.findAllByReturned(returned, pageRequest)).thenReturn(expectedResult);

        // then
        Page<LendTransactionView> response = lendTransactionService.findLendTransactionsByReturned(returned, page, size);
        assertEquals(expectedResult, response);

        verify(lendTransactionRepository, times(1)).findAllByReturned(returned, pageRequest);
    }

    @Test
    void testGetLateFeeById_whenGetLateFeeByIdCalledWithExistsId_shouldReturnLateFee() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = getTestLendTransaction(false);
        lendTransaction.setDeadlineDate(LocalDate.now().minusDays(2));

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));
        when(settingsService.getLateFeePerDay()).thenReturn(10.0);

        // then
        double result = lendTransactionService.getLateFeeById(id);
        assertEquals(20.0, result);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testGetLateFeeById_whenGetLateFeeByIdCalledWithExistsIdAndNoLateFee_shouldReturnZero() {
        // given
        UUID id = UUID.randomUUID();
        LendTransaction lendTransaction = getTestLendTransaction(false);
        lendTransaction.setDeadlineDate(LocalDate.now().plusDays(2));

        // when
        when(lendTransactionRepository.findById(id)).thenReturn(Optional.of(lendTransaction));
        when(settingsService.getLateFeePerDay()).thenReturn(10.0);

        // then
        double result = lendTransactionService.getLateFeeById(id);
        assertEquals(0.0, result);

        verify(lendTransactionRepository, times(1)).findById(id);
    }

    @Test
    void testCountByBookIdAndReturnedAndDeadlineDateAfter_whenCountByBookIdAndReturnedAndDeadlineDateAfterCalled_shouldReturnCount() {
        // given
        Long bookId = 1L;
        boolean returned = false;
        LocalDate date = LocalDate.now();
        int expectedResult = 1;

        // when
        when(lendTransactionRepository.countByBook_IdAndReturnedAndDeadlineDateBefore(bookId, returned, date)).thenReturn(expectedResult);

        // then
        long result = lendTransactionService.countByBookIdAndReturnedAndDeadlineDateAfter(bookId, returned, date);
        assertEquals(expectedResult, result);

        verify(lendTransactionRepository, times(1)).countByBook_IdAndReturnedAndDeadlineDateBefore(bookId, returned, date);
    }
}