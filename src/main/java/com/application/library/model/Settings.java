package com.application.library.model;

import com.application.library.enumerations.SettingsKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity(name = "settings")
@Table(name = "settings")
public class Settings {


    @Id
    @Column(name = "settings_key", nullable = false)
    private SettingsKey settingsKey;

    @Column(name = "settings_value")
    private String settingsValue;

    public SettingsKey getSettingsKey() {
        return settingsKey;
    }

    public void setSettingsKey(SettingsKey settingsKey) {
        this.settingsKey = settingsKey;
    }

    public String getSettingsValue() {
        return settingsValue;
    }

    public void setSettingsValue(String settingsValue) {
        this.settingsValue = settingsValue;
    }
}
