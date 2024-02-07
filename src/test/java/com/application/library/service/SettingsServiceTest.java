package com.application.library.service;

import com.application.library.enumerations.SettingsKey;
import com.application.library.model.Settings;
import com.application.library.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsServiceTest {

    private SettingsRepository settingsRepository;
    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        settingsRepository = mock(SettingsRepository.class);
        settingsService = new SettingsService(settingsRepository);
    }

    @Test
    void testGetLateFeePerDay_whenGetLateFeePerDayCalledWithExistsSettings_shouldReturnSettingsValue() {
        // given
        double expectedLateFeePerDay = 10.0;
        Settings settings = new Settings();
        settings.setSettingsKey(SettingsKey.LATE_FEE_PER_DAY);
        settings.setSettingsValue(String.valueOf(expectedLateFeePerDay));

        // when
        when(settingsRepository.findById(SettingsKey.LATE_FEE_PER_DAY)).thenReturn(Optional.of(settings));

        // then
        assertEquals(expectedLateFeePerDay, settingsService.getLateFeePerDay());

        verify(settingsRepository, times(1)).findById(SettingsKey.LATE_FEE_PER_DAY);
    }

    @Test
    void testGetLateFeePerDay_whenGetLateFeePerDayCalledWithNotExistsSettings_shouldReturnZero() {
        // given
        double expectedLateFeePerDay = 0.0;

        // when
        when(settingsRepository.findById(SettingsKey.LATE_FEE_PER_DAY)).thenReturn(Optional.empty());

        // then
        assertEquals(expectedLateFeePerDay, settingsService.getLateFeePerDay());

        verify(settingsRepository, times(1)).findById(SettingsKey.LATE_FEE_PER_DAY);
    }

    @Test
    void testSetLateFeePerDay_whenSetLateFeePerDayCalledWithExistsSettings_shouldReturnSettings() {
        // given
        double lateFeePerDay = 10.0;
        Settings settings = new Settings();
        settings.setSettingsKey(SettingsKey.LATE_FEE_PER_DAY);
        settings.setSettingsValue(String.valueOf(lateFeePerDay));

        // when
        when(settingsRepository.findById(SettingsKey.LATE_FEE_PER_DAY)).thenReturn(Optional.of(settings));
        when(settingsRepository.save(settings)).thenReturn(settings);

        // then
        assertEquals(settings, settingsService.setLateFeePerDay(lateFeePerDay));

        verify(settingsRepository, times(1)).findById(SettingsKey.LATE_FEE_PER_DAY);
        verify(settingsRepository, times(1)).save(settings);
    }

    @Test
    void testSetLateFeePerDay_whenSetLateFeePerDayCalledWithNotExistsSettings_shouldReturnSettings() {
        // given
        double lateFeePerDay = 10.0;
        Settings settings = new Settings();
        settings.setSettingsKey(SettingsKey.LATE_FEE_PER_DAY);
        settings.setSettingsValue(String.valueOf(lateFeePerDay));

        // when
        when(settingsRepository.findById(SettingsKey.LATE_FEE_PER_DAY)).thenReturn(Optional.empty());
        when(settingsRepository.save(any(Settings.class))).thenReturn(settings);

        // then
        Settings actual = settingsService.setLateFeePerDay(lateFeePerDay);
        assertEquals(settings, actual);

        verify(settingsRepository, times(1)).findById(SettingsKey.LATE_FEE_PER_DAY);
        verify(settingsRepository, times(1)).save(any(Settings.class));
    }

    @Test
    void testSetLendDay_whenSetLendDayCalledWithExistsSettings_shouldReturnSettings() {
        // given
        int lendDay = 10;
        Settings settings = new Settings();
        settings.setSettingsKey(SettingsKey.LEND_DAY);
        settings.setSettingsValue(String.valueOf(lendDay));

        // when
        when(settingsRepository.findById(SettingsKey.LEND_DAY)).thenReturn(Optional.of(settings));
        when(settingsRepository.save(settings)).thenReturn(settings);

        // then
        assertEquals(settings, settingsService.setLendDay(lendDay));

        verify(settingsRepository, times(1)).findById(SettingsKey.LEND_DAY);
        verify(settingsRepository, times(1)).save(settings);
    }

    @Test
    void testSetLendDay_whenSetLendDayCalledWithNotExistsSettings_shouldReturnSettings() {
        // given
        int lendDay = 10;
        Settings settings = new Settings();
        settings.setSettingsKey(SettingsKey.LEND_DAY);
        settings.setSettingsValue(String.valueOf(lendDay));

        // when
        when(settingsRepository.findById(SettingsKey.LEND_DAY)).thenReturn(Optional.empty());
        when(settingsRepository.save(any(Settings.class))).thenReturn(settings);

        // then
        assertEquals(settings, settingsService.setLendDay(lendDay));

        verify(settingsRepository, times(1)).findById(SettingsKey.LEND_DAY);
        verify(settingsRepository, times(1)).save(any(Settings.class));
    }

    @Test
    void testGetLendDay_whenGetLendDayCalledWithExistsSettings_shouldReturnSettingsValue() {
        // given
        int expectedLendDay = 10;
        Settings settings = new Settings();
        settings.setSettingsKey(SettingsKey.LEND_DAY);
        settings.setSettingsValue(String.valueOf(expectedLendDay));

        // when
        when(settingsRepository.findById(SettingsKey.LEND_DAY)).thenReturn(Optional.of(settings));

        // then
        assertEquals(expectedLendDay, settingsService.getLendDay());

        verify(settingsRepository, times(1)).findById(SettingsKey.LEND_DAY);
    }

    @Test
    void testGetLendDay_whenGetLendDayCalledWithNotExistsSettings_shouldReturn14() {
        // given
        int expectedLendDay = 14;

        // when
        when(settingsRepository.findById(SettingsKey.LEND_DAY)).thenReturn(Optional.empty());

        // then
        assertEquals(expectedLendDay, settingsService.getLendDay());

        verify(settingsRepository, times(1)).findById(SettingsKey.LEND_DAY);
    }
}