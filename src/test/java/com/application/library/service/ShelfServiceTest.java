package com.application.library.service;

import com.application.library.converter.ShelfConverter;
import com.application.library.data.dto.SaveShelfRequestDto;
import com.application.library.data.view.shelf.ShelfBaseView;
import com.application.library.data.view.shelf.ShelfView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.IllegalDeleteOperationException;
import com.application.library.exception.ShelfFullException;
import com.application.library.listener.event.UpdateShelfAvailableCapacityEvent;
import com.application.library.model.Book;
import com.application.library.model.Shelf;
import com.application.library.repository.ShelfRepository;
import com.application.library.support.TestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShelfServiceTest extends TestSupport {

    private ShelfRepository shelfRepository;
    private ShelfConverter shelfConverter;
    private ApplicationEventPublisher applicationEventPublisher;
    private ShelfService shelfService;

    @BeforeEach
    void setUp() {
        shelfRepository = mock(ShelfRepository.class);
        shelfConverter = mock(ShelfConverter.class);
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        shelfService = new ShelfService(shelfRepository, shelfConverter, applicationEventPublisher);
    }

    @Test
    void testSaveShelf_whenShelfNameDoesNotExist_shouldReturnShelf() {
        // given
        Shelf testShelf = getTestShelf();
        SaveShelfRequestDto requestDto = new SaveShelfRequestDto();
        requestDto.setName(testShelf.getName());


        // when
        when(shelfRepository.existsByName(requestDto.getName())).thenReturn(false);
        when(shelfConverter.toEntity(requestDto)).thenReturn(testShelf);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(testShelf);

        Shelf result = shelfService.saveShelf(requestDto);

        // then
        assertEquals(testShelf, result);

        verify(shelfRepository, times(1)).existsByName(requestDto.getName());
        verify(shelfConverter, times(1)).toEntity(requestDto);
        verify(shelfRepository, times(1)).save(any(Shelf.class));
    }

    @Test
    void testSaveTest_whenShelfNameAlreadyExists_shouldThrowEntityAlreadyExistsException() {
        // given
        SaveShelfRequestDto requestDto = new SaveShelfRequestDto();
        requestDto.setName("Shelf1");

        //when
        when(shelfRepository.existsByName(requestDto.getName())).thenReturn(true);

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> shelfService.saveShelf(requestDto));
    }

    @Test
    void testFindById_whenShelfExists_shouldReturnShelf() {
        // given
        Shelf testShelf = getTestShelf();

        // when
        when(shelfRepository.findById(testShelf.getId())).thenReturn(java.util.Optional.of(testShelf));

        Shelf result = shelfService.findById(testShelf.getId());

        // then
        assertEquals(testShelf, result);

        verify(shelfRepository, times(1)).findById(testShelf.getId());
    }

    @Test
    void testFindById_whenShelfDoesNotExist_shouldThrowEntityNotFoundException() {
        // given
        Long shelfId = 1L;

        // when
        when(shelfRepository.findById(shelfId)).thenReturn(java.util.Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> shelfService.findById(shelfId));
    }

    @Test
    void testGetShelfById_whenShelfExists_shouldReturnShelfView() {
        // given
        ShelfView testShelf = getTestShelfView();

        // when
        when(shelfRepository.getShelfById(testShelf.getId())).thenReturn(java.util.Optional.of(testShelf));

        ShelfView result = shelfService.getShelfById(testShelf.getId());

        // then
        assertEquals(testShelf, result);

        verify(shelfRepository, times(1)).getShelfById(testShelf.getId());
    }

    @Test
    void testGetShelfById_whenShelfDoesNotExist_shouldThrowEntityNotFoundException() {
        // given
        Long shelfId = 1L;

        // when
        when(shelfRepository.getShelfById(shelfId)).thenReturn(java.util.Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> shelfService.getShelfById(shelfId));
    }

    @Test
    void testDeleteShelf_whenShelfIsEmpty_shouldDeleteShelf() {
        // given
        Shelf testShelf = getTestShelf();

        // when
        when(shelfRepository.findById(testShelf.getId())).thenReturn(java.util.Optional.of(testShelf));

        shelfService.deleteShelf(testShelf.getId());

        // then
        verify(shelfRepository, times(1)).delete(testShelf);
    }

    @Test
    void testDeleteShelf_whenShelfIsNotEmpty_shouldThrowIllegalDeleteOperationException() {
        // given
        Shelf testShelf = getTestShelf();
        testShelf.setBooks(Set.of(new Book()));

        // when
        when(shelfRepository.findById(testShelf.getId())).thenReturn(java.util.Optional.of(testShelf));

        // then
        assertThrows(IllegalDeleteOperationException.class, () -> shelfService.deleteShelf(testShelf.getId()));
    }

    @Test
    void testUpdateShelf_whenShelfExists_shouldReturnUpdatedShelf() {
        // given
        Shelf testShelf = getTestShelf();
        SaveShelfRequestDto requestDto = new SaveShelfRequestDto();
        requestDto.setCapacity(20);

        // when
        when(shelfRepository.findById(testShelf.getId())).thenReturn(java.util.Optional.of(testShelf));
        when(shelfConverter.updateEntity(requestDto, testShelf)).thenReturn(testShelf);

        Shelf result = shelfService.updateShelf(testShelf.getId(), requestDto);

        // then
        assertEquals(testShelf, result);

        verify(shelfRepository, times(1)).findById(testShelf.getId());
        verify(shelfConverter, times(1)).updateEntity(requestDto, testShelf);
        verify(applicationEventPublisher, times(1)).publishEvent(any(UpdateShelfAvailableCapacityEvent.class));
    }

    @Test
    void testGetAllShelf_shouldReturnAllShelves() {
        // given
        ShelfBaseView testShelf = getTestShelfBaseView();
        Page<ShelfBaseView> expectedResult = new PageImpl<>(List.of(testShelf));

        // when
        when(shelfRepository.getAllBy(any(PageRequest.class))).thenReturn(expectedResult);

        // then
        Page<ShelfBaseView> result = shelfService.getAllShelf(0, 10);
        assertEquals(expectedResult, result);

        verify(shelfRepository, times(1)).getAllBy(any(PageRequest.class));
    }

    @Test
    void testCheckShelfCapacity_whenShelfIsNotFull_shouldNotThrowException() {
        // given
        Shelf testShelf = getTestShelf();
        testShelf.setAvailableCapacity(5);

        // then
        assertDoesNotThrow(() -> shelfService.checkShelfCapacity(testShelf));
    }

    @Test
    void testCheckShelfCapacity_whenShelfIsFull_shouldThrowShelfFullException() {
        // given
        Shelf testShelf = getTestShelf();
        testShelf.setAvailableCapacity(0);

        // then
        assertThrows(ShelfFullException.class, () -> shelfService.checkShelfCapacity(testShelf));
    }

    @Test
    void testCheckShelfCapacity_whenShelfWillBeFull_shouldThrowShelfFullException() {
        // given
        Shelf testShelf = getTestShelf();
        testShelf.setBooks(Set.of(new Book(), new Book(), new Book(), new Book()));
        int newCapacity = 3;

        // then
        assertThrows(ShelfFullException.class, () -> shelfService.checkShelfCapacity(testShelf, newCapacity));
    }

    @Test
    void testCheckShelfCapacity_whenShelfWillNotBeFull_shouldNotThrowException() {
        // given
        Shelf testShelf = getTestShelf();
        testShelf.setBooks(Set.of(new Book(), new Book(), new Book()));
        int newCapacity = 5;

        // then
        assertDoesNotThrow(() -> shelfService.checkShelfCapacity(testShelf, newCapacity));
    }
}