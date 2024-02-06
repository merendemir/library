package com.application.library.service;

import com.application.library.data.view.ReadingListView;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.helper.AuthHelper;
import com.application.library.model.Book;
import com.application.library.model.ReadingList;
import com.application.library.model.User;
import com.application.library.repository.ReadingListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ReadingListService {

    private final ReadingListRepository readingListRepository;
    private final BookService bookService;

    public ReadingListService(ReadingListRepository readingListRepository, BookService bookService) {
        this.readingListRepository = readingListRepository;
        this.bookService = bookService;
    }

    @Transactional
    public ReadingList addBookToReadingList(Long bookId) {
        User activeUser = AuthHelper.getActiveUser();
        Book book = bookService.findById(bookId);

        return readingListRepository.findByUser_Id(activeUser.getId())
                .map(readingList -> {
                    readingList.getBooks().add(book);
                    return readingListRepository.save(readingList);
                })
                .orElseGet(() -> readingListRepository.save(new ReadingList(activeUser, Set.of(book))));
    }

    @Transactional
    public ReadingList removeBookFromReadingList(Long bookId) {
        User activeUser = AuthHelper.getActiveUser();
        Book book = bookService.findById(bookId);

        return readingListRepository.findByUser_Id(activeUser.getId())
                .map(readingList -> {
                    readingList.getBooks().remove(book);
                    return readingListRepository.save(readingList);
                })
                .orElseThrow(() -> new EntityNotFoundException("Reading list not found"));
    }

    @Transactional(readOnly = true)
    public Page<ReadingListView> getReadingList(int page, int size) {
        User activeUser = AuthHelper.getActiveUser();
        PageRequest pageRequest = PageRequest.of(page, size);
        return readingListRepository.findByUser_Id(activeUser.getId(), pageRequest);
    }

    @Transactional
    public Long clearReadingList() {
        User activeUser = AuthHelper.getActiveUser();
        readingListRepository.findByUser_Id(activeUser.getId())
                .ifPresent(readingList -> {
                    readingList.getBooks().clear();
                    readingListRepository.save(readingList);
                });

        return activeUser.getId();
    }
}

