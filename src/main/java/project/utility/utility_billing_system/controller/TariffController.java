package project.utility.utility_billing_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.utility.utility_billing_system.entity.MeterType;
import project.utility.utility_billing_system.entity.Tariff;
import project.utility.utility_billing_system.service.TariffService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    @Autowired
    private TariffService tariffService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tariff> configureTariff(@Valid @RequestBody Tariff tariff) {
        Tariff savedTariff = tariffService.configureTariff(tariff);
        return new ResponseEntity<>(savedTariff, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR', 'CUSTOMER')")
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        List<Tariff> tariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(tariffs);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR', 'CUSTOMER')")
    public ResponseEntity<Tariff> getActiveTariff(
            @RequestParam MeterType meterType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Tariff tariff = tariffService.getActiveTariffForDate(meterType, date);
        return ResponseEntity.ok(tariff);
    }
}
