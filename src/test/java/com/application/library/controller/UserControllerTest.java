package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.UserSaveRequestDto;
import com.application.library.data.view.UserListView;
import com.application.library.data.view.UserView;
import com.application.library.enumerations.UserRole;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.User;
import com.application.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseRestControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testSaveUser_whenSaveUserCalledWithUserSaveRequestDto_shouldReturnUserId() throws Exception {
        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto();
        User testUser = getTestUser();


        when(userService.saveUser(userSaveRequestDto)).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSaveRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", is(testUser.getId().intValue())));
    }

    @Test
    void testSaveUser_whenSaveUserCalledWithExistsUser_shouldReturnHTTP409() throws Exception {
        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto();
        String errorMessage = MessageConstants.USER_ALREADY_EXISTS_WITH_EMAIL;

        when(userService.saveUser(userSaveRequestDto)).thenThrow(new EntityAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSaveRequestDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetUser_whenGetUserCalledWithUserId_shouldReturnUser() throws Exception {
        UserView testUser = getTestUserView();
        Long userId = testUser.getId();

        when(userService.getUserById(userId)).thenReturn(getTestUserView());

        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.data.email", is(testUser.getEmail())))
                .andExpect(jsonPath("$.data.firstName", is(testUser.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", is(testUser.getLastName())));
    }

    @Test
    void testGetUser_whenGetUserCalledWithNotExistsUserId_shouldReturnHTTP404() throws Exception {
        Long userId = -1L;
        String errorMessage = MessageConstants.USER_NOT_FOUND;

        when(userService.getUserById(userId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testGetAllUsers_whenGetAllUsersCalled_shouldReturnUserList() throws Exception {
        UserListView testUser = getTestUserListView();
        int page = 0;
        int size = 10;

        List<UserListView> testUserListViewList = List.of(testUser);

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<UserListView> userViewPage = new PageImpl<>(testUserListViewList, pageable, testUserListViewList.size());

        when(userService.getAllUsersByActiveUserAuthority(Optional.empty(), page, size, Optional.empty(), Optional.empty())).thenReturn(userViewPage);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].email", is(testUser.getEmail())))
                .andExpect(jsonPath("$.data.content[0].firstName", is(testUser.getFirstName())))
                .andExpect(jsonPath("$.data.content[0].lastName", is(testUser.getLastName())));
    }

    @Test
    void testGetAllUsers_whenGetAllUsersCalledWithUserRole_shouldReturnUserList() throws Exception {
        UserView testUser = getTestUserView();
        int page = 0;
        int size = 10;
        String userType = "ROLE_USER";

        List<UserListView> testUserListViewList = List.of(getTestUserListView());

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<UserListView> userViewPage = new PageImpl<>(testUserListViewList, pageable, testUserListViewList.size());

        when(userService.getAllUsersByActiveUserAuthority(Optional.of(UserRole.ROLE_USER), page, size, Optional.empty(), Optional.empty())).thenReturn(userViewPage);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("userType", userType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].email", is(testUser.getEmail())))
                .andExpect(jsonPath("$.data.content[0].firstName", is(testUser.getFirstName())))
                .andExpect(jsonPath("$.data.content[0].lastName", is(testUser.getLastName())));
    }

    @Test
    void testDeleteUser_whenDeleteUserCalledWithUserId_shouldReturnUserId() throws Exception {
        User testUser = getTestUser();
        Long userId = testUser.getId();

        when(userService.deleteUserByActiveUserAuthority(userId)).thenReturn(userId);

        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testUser.getId().intValue())));
    }

    @Test
    void testDeleteUser_whenDeleteUserCalledWithNotExistsUserId_shouldReturnHTTP404() throws Exception {
        Long userId = -1L;
        String errorMessage = MessageConstants.USER_NOT_FOUND;

        when(userService.deleteUserByActiveUserAuthority(userId)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testDeleteUser_whenDeleteUserCalledWithLibrarian_shouldReturnHTTP403() throws Exception {
        User testUser = getTestUser();
        Long userId = testUser.getId();
        String errorMessage = MessageConstants.NOT_AUTHORIZED_FOR_DELETE_LIBRARIAN;

        when(userService.deleteUserByActiveUserAuthority(userId)).thenThrow(new AccessDeniedException(errorMessage));

        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateUser_whenUpdateUserCalledWithUserIdAndUserSaveRequestDto_shouldReturnUser() throws Exception {
        User testUser = getTestUser();
        Long userId = testUser.getId();
        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto();

        when(userService.updateUser(userId, userSaveRequestDto)).thenReturn(testUser);

        mockMvc.perform(put("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSaveRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testUser.getId().intValue())));
    }

    @Test
    void testUpdateUser_whenUpdateUserCalledWithNotExistsUserId_shouldReturnHTTP404() throws Exception {
        Long userId = -1L;
        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto();
        String errorMessage = MessageConstants.USER_NOT_FOUND;

        when(userService.updateUser(userId, userSaveRequestDto)).thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userSaveRequestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testUpdateActiveUserInfo_whenUpdateActiveUserInfoCalledWithBaseUserSaveRequestDto_shouldReturnUser() throws Exception {
        User testUser = getTestUser();
        BaseUserSaveRequestDto baseUserSaveRequestDto = new BaseUserSaveRequestDto();

        when(userService.updateActiveUserInfo(baseUserSaveRequestDto)).thenReturn(testUser);

        mockMvc.perform(put("/api/users/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(baseUserSaveRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(testUser.getId().intValue())));
    }

}