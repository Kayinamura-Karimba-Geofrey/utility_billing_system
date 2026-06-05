package project.utility.utility_billing_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tariffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Meter type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false)
    private MeterType meterType;

    @NotBlank(message = "Tariff name is required")
    @Column(name = "tariff_name", nullable = false)
    private String tariffName;

    @NotNull(message = "Rate per unit is required")
    @PositiveOrZero(message = "Rate per unit cannot be negative")
    @Column(name = "rate_per_unit", nullable = false)
    private Double ratePerUnit;

    @NotNull(message = "Fixed service charge is required")
    @PositiveOrZero(message = "Fixed service charge cannot be negative")
    @Column(name = "fixed_service_charge", nullable = false)
    private Double fixedServiceCharge;

    @NotNull(message = "VAT rate is required")
    @PositiveOrZero(message = "VAT rate cannot be negative")
    @Column(name = "vat_rate", nullable = false)
    private Double vatRate;

    @NotNull(message = "Penalty rate is required")
    @PositiveOrZero(message = "Penalty rate cannot be negative")
    @Column(name = "penalty_rate", nullable = false)
    private Double penaltyRate;

    @NotNull(message = "Version is required")
    @Column(nullable = false)
    private Integer version;

    @NotNull(message = "Active from date is required")
    @Column(name = "active_from", nullable = false)
    private LocalDate activeFrom;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
}
