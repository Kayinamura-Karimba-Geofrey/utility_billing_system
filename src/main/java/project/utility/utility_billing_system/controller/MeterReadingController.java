package project.utility.utility_billing_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.utility.utility_billing_system.dto.ReadingRequest;
import project.utility.utility_billing_system.entity.MeterReading;
import project.utility.utility_billing_system.service.MeterReadingService;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<MeterReading> captureReading(
            @Valid @RequestBody ReadingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MeterReading savedReading = meterReadingService.captureReading(request, userDetails.getUsername());
        return new ResponseEntity<>(savedReading, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
    public ResponseEntity<List<MeterReading>> getAllReadings() {
        List<MeterReading> readings = meterReadingService.getAllReadings();
        return ResponseEntity.ok(readings);
    }
}
