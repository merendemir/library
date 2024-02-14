package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.SaveShelfRequestDto;
import com.application.library.data.view.shelf.ShelfBaseView;
import com.application.library.data.view.shelf.ShelfView;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.Shelf;
import com.application.library.service.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShelfControllerTest extends BaseRestControllerTest {

    @MockBean
    private ShelfService shelfService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ShelfController(shelfService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testSaveShelf_whenSaveShelfCalledWithSaveShelfRequest_shouldReturnSavedShelfId() throws Exception {
        // given
        Shelf testShelf = getTestShelf();
        SaveShelfRequestDto saveShelfRequestDto = new SaveShelfRequestDto();

        when(shelfService.saveShelf(saveShelfRequestDto)).thenReturn(testShelf);

        mockMvc.perform(post("/api/shelves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveShelfRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", is(testShelf.getId().intValue())));
    }

    @Test
    void testSaveShelf_whenSaveShelfCalledWithExistsName_shouldReturnHTTP409() throws Exception {
        // given
        SaveShelfRequestDto saveShelfRequestDto = new SaveShelfRequestDto();
        String errorMessage = MessageConstants.SHELF_ALREADY_EXISTS_WITH_NAME;
        when(shelfService.saveShelf(saveShelfRequestDto)).thenThrow(new EntityAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/api/shelves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveShelfRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetShelf_whenGetShelfCalledWithId_shouldReturnShelf() throws Exception {
        // given
        ShelfView testShelf = getTestShelfView();
        when(shelfService.getShelfById(testShelf.getId())).thenReturn(testShelf);

        mockMvc.perform(get("/api/shelves/{id}", testShelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(testShelf.getId().intValue())))
                .andExpect(jsonPath("$.data.name", is(testShelf.getName())));
    }

    @Test
    void testGetShelf_whenGetShelfCalledWithNonExistsId_shouldReturnHTTP404() throws Exception {
        // given
        Long nonExistsId = -1L;
        String errorMessage = MessageConstants.SHELF_NOT_FOUND;
        when(shelfService.getShelfById(nonExistsId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(get("/api/shelves/{id}", nonExistsId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetAllShelves_whenGetAllShelvesCalled_shouldReturnShelves() throws Exception {
        // given
        int page = 0;
        int size = 10;

        List<ShelfBaseView> shelfViewList = List.of(getTestShelfBaseView());

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<ShelfBaseView> shelfViewPage = new PageImpl<>(shelfViewList, pageable, shelfViewList.size());

        when(shelfService.getAllShelf(page, size)).thenReturn(shelfViewPage);

        mockMvc.perform(get("/api/shelves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(shelfViewList.get(0).getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].name", is(shelfViewList.get(0).getName())));
    }

    @Test
    void testDeleteShelf_whenDeleteShelfCalledWithId_shouldReturnShelfId() throws Exception {
        // given
        Long shelfId = 1L;
        when(shelfService.deleteShelf(shelfId)).thenReturn(shelfId);

        mockMvc.perform(delete("/api/shelves/{id}", shelfId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(shelfId.intValue())));
    }

    @Test
    void testDeleteShelf_whenDeleteShelfCalledWithNonExistsId_shouldReturnHTTP404() throws Exception {
        // given
        Long nonExistsId = -1L;
        String errorMessage = MessageConstants.SHELF_NOT_FOUND;
        when(shelfService.deleteShelf(nonExistsId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete("/api/shelves/{id}", nonExistsId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testDeleteShelf_whenDeleteShelfCalledWithNotEmptyShelf_shouldReturnHTTP400() throws Exception {
        // given
        Long shelfId = 1L;
        String errorMessage = MessageConstants.SHELF_CANNOT_BE_DELETED;
        when(shelfService.deleteShelf(shelfId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete("/api/shelves/{id}", shelfId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateShelf_whenUpdateShelfCalledWithIdAndSaveShelfRequest_shouldReturnUpdatedShelfId() throws Exception {
        // given
        Long shelfId = 1L;
        SaveShelfRequestDto saveShelfRequestDto = new SaveShelfRequestDto();
        Shelf testShelf = getTestShelf();

        when(shelfService.updateShelf(shelfId, saveShelfRequestDto)).thenReturn(testShelf);

        mockMvc.perform(put("/api/shelves/{id}", shelfId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveShelfRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testShelf.getId().intValue())));
    }

    @Test
    void testUpdateShelf_whenUpdateShelfCalledWithNonExistsId_shouldReturnHTTP404() throws Exception {
        // given
        Long nonExistsId = -1L;
        SaveShelfRequestDto saveShelfRequestDto = new SaveShelfRequestDto();
        String errorMessage = MessageConstants.SHELF_NOT_FOUND;
        when(shelfService.updateShelf(nonExistsId, saveShelfRequestDto)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/api/shelves/{id}", nonExistsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(saveShelfRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }



}