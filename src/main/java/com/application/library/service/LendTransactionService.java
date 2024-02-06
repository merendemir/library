package com.application.library.service;


import com.application.library.converter.LendTransactionConverter;
import com.application.library.data.dto.LendTransactionRequestDto;
import com.application.library.data.view.transaction.lend.LendTransactionAuthUserView;
import com.application.library.data.view.transaction.lend.LendTransactionView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.listener.event.UpdateBookAvailableCountEvent;
import com.application.library.listener.event.UpdateUserReservationCompleteStatus;
import com.application.library.model.LendTransaction;
import com.application.library.repository.LendTransactionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LendTransactionService {

    private final LendTransactionRepository lendTransactionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LendTransactionConverter lendTransactionConverter;
    private final SettingsService settingsService;

    public LendTransactionService(LendTransactionRepository lendTransactionRepository, ApplicationEventPublisher applicationEventPublisher, LendTransactionConverter lendTransactionConverter, SettingsService settingsService) {
        this.lendTransactionRepository = lendTransactionRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.lendTransactionConverter = lendTransactionConverter;
        this.settingsService = settingsService;
    }

    @Transactional
    public LendTransaction lendBook(LendTransactionRequestDto requestDto) {
        LendTransaction lendTransaction = lendTransactionRepository.save(lendTransactionConverter.toEntity(requestDto));
        applicationEventPublisher.publishEvent(new UpdateBookAvailableCountEvent(this, lendTransaction.getBook().getId()));
        applicationEventPublisher.publishEvent(new UpdateUserReservationCompleteStatus(this, lendTransaction.getBook().getId(), lendTransaction.getUser().getId()));
        return lendTransaction;
    }

    @Transactional
    public LendTransaction returnBook(UUID id) {
        LendTransaction lendTransaction = findById(id);

        if (lendTransaction.isReturned()) throw new IllegalStateException("Book is already returned");

        double calculatedLateFee = calculateLateFee(lendTransaction);

        if (calculatedLateFee > 0) throw new IllegalStateException("You have to pay late fee: " + calculatedLateFee);

        lendTransaction.setReturned(true);
        lendTransaction.setReturnDate(LocalDateTime.now());

        applicationEventPublisher.publishEvent(new UpdateBookAvailableCountEvent(this, lendTransaction.getBook().getId()));
        return lendTransaction;
    }

    @Transactional(readOnly = true)
    public LendTransaction findById(UUID id) {
        return lendTransactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lend transaction not found"));
    }

    @Transactional
    public LendTransaction payLateFee(UUID id) {
        LendTransaction lendTransaction = findById(id);
        double calculatedLateFee = calculateLateFee(lendTransaction);

        if (calculatedLateFee <= 0) throw new IllegalStateException("No late fee to pay");

        double payedFee = lendTransaction.getLateFeePaid() == null ? 0 : lendTransaction.getLateFeePaid();
        lendTransaction.setLateFeePaid(calculatedLateFee + payedFee);

        return lendTransaction;
    }

    @Transactional(readOnly = true)
    public Page<LendTransactionAuthUserView> findLendTransactionsForAuthUser(int page, int size) {
        return findLendTransactionsByUserId(AuthHelper.getActiveUser().getId(), page, size);
    }

    @Transactional(readOnly = true)
    public Page<LendTransactionAuthUserView> findLendTransactionsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lendTransactionRepository.findAllByUser_Id(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<LendTransactionView> findLendTransactionsByReturned(boolean returned, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lendTransactionRepository.findAllByReturned(returned, pageable);
    }

    @Transactional(readOnly = true)
    public double getLateFeeById(UUID id) {
        LendTransaction lendTransaction = findById(id);
        return calculateLateFee(lendTransaction);
    }

    private double calculateLateFee(LendTransaction lendTransaction) {
        LocalDate deadlineDate = lendTransaction.getDeadlineDate();
        LocalDate now = LocalDate.now();

        if (deadlineDate.isEqual(now) || deadlineDate.isAfter(now)) return 0.0;

        double lateFeePerDay = settingsService.getLateFeePerDay();
        long daysLate = now.toEpochDay() - deadlineDate.toEpochDay();
        double totalFee = lateFeePerDay * daysLate;

        double lateFeePaid = lendTransaction.getLateFeePaid() == null ? 0 : lendTransaction.getLateFeePaid();
        return totalFee - lateFeePaid;
    }

    @Transactional(readOnly = true)
    public long countByBookIdAndReturnedAndDeadlineDateAfter(Long bookId, boolean returned, LocalDate date) {
        return lendTransactionRepository.countByBook_IdAndReturnedAndDeadlineDateBefore(bookId, returned, date);
    }

}
