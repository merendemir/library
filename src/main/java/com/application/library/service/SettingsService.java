package com.application.library.service;


import com.application.library.config.CachingConfig;
import com.application.library.enumerations.SettingsKey;
import com.application.library.model.Settings;
import com.application.library.repository.SettingsRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Cacheable(value = CachingConfig.LATE_FEE_PER_DAY)
    @Transactional(readOnly = true)
    public double getLateFeePerDay() {
        Optional<Settings> lateFeePerDaySettings = settingsRepository.findById(SettingsKey.LATE_FEE_PER_DAY);
        return lateFeePerDaySettings.map(settings -> Double.parseDouble(settings.getSettingsValue())).orElse(0.0);
    }

    @CacheEvict(value = CachingConfig.LATE_FEE_PER_DAY, allEntries = true)
    @Transactional
    public Settings setLateFeePerDay(double lateFeePerDay) {
        Optional<Settings> optionalSettings = settingsRepository.findById(SettingsKey.LATE_FEE_PER_DAY);
        Settings settings;
        if (optionalSettings.isPresent()) {
            settings = optionalSettings.get();
        } else {
            settings = new Settings();
            settings.setSettingsKey(SettingsKey.LATE_FEE_PER_DAY);
        }

        settings.setSettingsValue(String.valueOf(lateFeePerDay));
        return settingsRepository.save(settings);
    }

}
