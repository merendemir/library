package com.application.library.repository;


import com.application.library.enumerations.SettingsKey;
import com.application.library.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, SettingsKey> {

}
