package com.application.library.controller;

import com.application.library.constants.MessageConstants;
import com.application.library.data.dto.user.BaseUserDto;
import com.application.library.data.dto.user.BaseUserSaveRequestDto;
import com.application.library.data.dto.user.LoginRequestDto;
import com.application.library.data.dto.user.LoginResponseDto;
import com.application.library.exception.EntityAlreadyExistsException;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.User;
import com.application.library.service.AuthService;
import com.application.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseRestControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, userService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testRegister_whenRegisterCalledWithBaseUserSaveRequestDto_shouldReturnUserId() throws Exception {
        User testUser = getTestUser();
        BaseUserSaveRequestDto baseUserSaveRequestDto = new BaseUserSaveRequestDto();

        when(userService.saveBaseUser(baseUserSaveRequestDto)).thenReturn(testUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(baseUserSaveRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data", is(testUser.getId().intValue())));
    }

    @Test
    void testRegister_whenRegisterCalledWithExistsUser_shouldReturnHTTP409() throws Exception {
        BaseUserSaveRequestDto baseUserSaveRequestDto = new BaseUserSaveRequestDto();
        String errorMessage = MessageConstants.USER_ALREADY_EXISTS_WITH_EMAIL;

        when(userService.saveBaseUser(baseUserSaveRequestDto)).thenThrow(new EntityAlreadyExistsException(errorMessage));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(baseUserSaveRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is(errorMessage)));
    }

    @Test
    void testLogin_whenLoginCalledWithLoginRequestDto_shouldReturnLoginResponseDto() throws Exception {
        User testUser = getTestUser();

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(testUser.getEmail());
        loginRequestDto.setPassword(testUser.getPassword());

        BaseUserDto baseUserDto = new BaseUserDto();
        baseUserDto.setId(testUser.getId());
        baseUserDto.setEmail(testUser.getEmail());
        baseUserDto.setFirstName(testUser.getFirstName());
        baseUserDto.setLastName(testUser.getLastName());

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setUser(baseUserDto);

        when(authService.login(loginRequestDto)).thenReturn(loginResponseDto);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.id", is(baseUserDto.getId().intValue())))
                .andExpect(jsonPath("$.data.user.email", is(baseUserDto.getEmail())))
                .andExpect(jsonPath("$.data.user.firstName", is(baseUserDto.getFirstName())))
                .andExpect(jsonPath("$.data.user.lastName", is(baseUserDto.getLastName())));
    }

    @Test
    void testLogin_whenLoginCalledWithInvalidCredentials_shouldReturnHTTP401() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        when(authService.login(loginRequestDto)).thenThrow(new AuthenticationServiceException(null));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }
}