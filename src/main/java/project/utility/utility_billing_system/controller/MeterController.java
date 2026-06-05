package project.utility.utility_billing_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.utility.utility_billing_system.entity.Meter;
import project.utility.utility_billing_system.service.MeterService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MeterController {

    @Autowired
    private MeterService meterService;

    @PostMapping("/customers/{customerId}/meters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Meter> createMeter(@PathVariable Long customerId, @Valid @RequestBody Meter meter) {
        Meter savedMeter = meterService.saveMeter(customerId, meter);
        return new ResponseEntity<>(savedMeter, HttpStatus.CREATED);
    }

    @GetMapping("/customers/{customerId}/meters")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR', 'CUSTOMER')")
    public ResponseEntity<List<Meter>> getMetersByCustomerId(@PathVariable Long customerId) {
        List<Meter> meters = meterService.getMetersByCustomerId(customerId);
        return ResponseEntity.ok(meters);
    }

    @GetMapping("/meters/{meterNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR', 'CUSTOMER')")
    public ResponseEntity<Meter> getMeterByNumber(@PathVariable String meterNumber) {
        Meter meter = meterService.getMeterByNumber(meterNumber);
        return ResponseEntity.ok(meter);
    }
}
