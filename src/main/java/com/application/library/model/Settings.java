package com.application.library.model;

import com.application.library.enumerations.SettingsKey;
import jakarta.persistence.*;


@Entity(name = "settings")
@Table(name = "settings")
public class Settings {


    @Id
    @Column(name = "settings_key", nullable = false)
    @Enumerated(EnumType.STRING)
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
