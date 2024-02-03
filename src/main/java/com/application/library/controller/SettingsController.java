package com.application.library.controller;


import com.application.library.enumerations.SettingsKey;
import com.application.library.service.SettingsService;
import com.application.library.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings", description = "Settings related endpoints")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PostMapping("/late/fee")
    public ResponseEntity<ResponseHandler<SettingsKey>> setLateFeePerDay(@RequestParam double lateFeePerDay) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(settingsService.setLateFeePerDay(lateFeePerDay).getSettingsKey()));
    }

    @GetMapping("/late/fee")
    public ResponseEntity<ResponseHandler<Double>> getLateFeePerDay() {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(settingsService.getLateFeePerDay()));
    }
}
