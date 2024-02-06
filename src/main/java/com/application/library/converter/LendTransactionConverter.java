package com.application.library.converter;

import com.application.library.data.dto.LendTransactionRequestDto;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.Book;
import com.application.library.model.LendTransaction;
import com.application.library.model.User;
import com.application.library.repository.BookReservationRepository;
import com.application.library.repository.LendTransactionRepository;
import com.application.library.service.BookService;
import com.application.library.service.SettingsService;
import com.application.library.service.UserService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {}
)
public abstract class LendTransactionConverter {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private LendTransactionRepository lendTransactionRepository;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private BookReservationRepository bookReservationRepository;

    @Mapping(target = "book", source = "dto.bookId", qualifiedByName = "idToBook")
    @Mapping(target = "user", source = "dto.userId", qualifiedByName = "idToUser")
    @Mapping(target = "lender", source = "dto.bookId", qualifiedByName = "setLender")
    @Mapping(target = "deadlineDate", source = "dto.bookId", qualifiedByName = "setDeadlineDate")
    public abstract LendTransaction toEntity(LendTransactionRequestDto dto);

    @Named("idToBook")
    Book idToBook(Long id) {
        if (id == null) return null;
        Book book = bookService.findById(id);
        if (!book.isAvailable()) throw new EntityAlreadyExistsException("Book is not available for lending");

        long reservedCount = bookReservationRepository.countByBook_IdAndReservationDateBeforeAndCompletedFalse(id, LocalDate.now().plusDays(settingsService.getLendDay()));
        if (reservedCount >= book.getAvailableCount())
            throw new EntityAlreadyExistsException("Book has a reservation");

        return book;
    }

    @Named("idToUser")
    User idToUser(Long id) {
        if (id == null) return null;
        User user = userService.findById(id);
        if (lendTransactionRepository.existsByUser_IdAndReturnedFalse(user.getId()))
            throw new EntityAlreadyExistsException("User has already lent a book");
        return user;
    }

    @Named("setLender")
    User setLender(Long id) {
        return AuthHelper.getActiveUser();
    }

    @Named("setDeadlineDate")
    LocalDate setDeadlineDate(Long id) {
        return LocalDate.now().plusDays(settingsService.getLendDay());
    }

}
