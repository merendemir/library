package com.application.library.controller;


import com.application.library.enumerations.SettingsKey;
import com.application.library.service.SettingsService;
import com.application.library.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
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

    @Operation(summary = "Set late fee per day", description = "Set late fee per day")
    @RolesAllowed({"ADMIN"})
    @PostMapping("/late/fee")
    public ResponseEntity<ResponseHandler<SettingsKey>> setLateFeePerDay(@RequestParam double lateFeePerDay) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(settingsService.setLateFeePerDay(lateFeePerDay).getSettingsKey()));
    }

    @Operation(summary = "Get late fee per day", description = "Get late fee per day")
    @GetMapping("/late/fee")
    public ResponseEntity<ResponseHandler<Double>> getLateFeePerDay() {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(settingsService.getLateFeePerDay()));
    }

    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Set lend day", description = "Set lend day")
    @PostMapping("/lend/day")
    public ResponseEntity<ResponseHandler<SettingsKey>> setLendDay(@RequestParam int lendDay) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(settingsService.setLendDay(lendDay).getSettingsKey()));
    }

    @Operation(summary = "Get lend day", description = "Get lend day")
    @GetMapping("/lend/day")
    public ResponseEntity<ResponseHandler<Integer>> getLendDay() {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseHandler<>(settingsService.getLendDay()));
    }

}
