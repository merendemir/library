package com.application.library.controller;

import com.application.library.enumerations.SettingsKey;
import com.application.library.exception.handler.DefaultExceptionHandler;
import com.application.library.model.Settings;
import com.application.library.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SettingsControllerTest extends BaseRestControllerTest {

    @MockBean
    private SettingsService settingsService;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SettingsController(settingsService))
                .setControllerAdvice(DefaultExceptionHandler.class)
                .build();
    }

    @Test
    void testSetLateFeePerDay_whenSetLateFeePerDayCalledWithValidLateFee_shouldReturnSettingKey() throws Exception {
        double lateFee = 10;
        Settings settings = new Settings();
        settings.setSettingsValue(String.valueOf(lateFee));
        settings.setSettingsKey(SettingsKey.LATE_FEE_PER_DAY);

        when(settingsService.setLateFeePerDay(lateFee)).thenReturn(settings);

        mockMvc.perform(post("/api/settings//late/fee")
                        .param("lateFeePerDay", String.valueOf(lateFee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(lateFee)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(SettingsKey.LATE_FEE_PER_DAY.name())));
    }

    @Test
    void testGetLateFeePerDay_whenGetLateFeePerDayCalled_shouldReturnLateFee() throws Exception {
        double lateFee = 10;
        when(settingsService.getLateFeePerDay()).thenReturn(lateFee);

        mockMvc.perform(get("/api/settings/late/fee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(lateFee)));
    }

    @Test
    void testSetLendDay_whenSetLendDaysCalledWithValidLendDays_shouldReturnSettingKey() throws Exception {
        int lendDays = 10;
        Settings settings = new Settings();
        settings.setSettingsValue(String.valueOf(lendDays));
        settings.setSettingsKey(SettingsKey.LEND_DAY);

        when(settingsService.setLendDay(lendDays)).thenReturn(settings);

        mockMvc.perform(post("/api/settings/lend/day")
                        .param("lendDay", String.valueOf(lendDays))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(lendDays)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(SettingsKey.LEND_DAY.name())));
    }

    @Test
    void testGetLendDay_whenGetLendDaysCalled_shouldReturnLendDays() throws Exception {
        int lendDays = 10;
        when(settingsService.getLendDay()).thenReturn(lendDays);

        mockMvc.perform(get("/api/settings/lend/day")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(lendDays)));
    }
}