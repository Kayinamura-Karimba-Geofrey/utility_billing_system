package project.utility.utility_billing_system.entity;

import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false)
    private MeterType meterType;

    @Column(name = "tariff_name", nullable = false)
    private String tariffName;

    @Column(name = "rate_per_unit", nullable = false)
    private Double ratePerUnit;

    @Column(name = "fixed_service_charge", nullable = false)
    private Double fixedServiceCharge;

    @Column(name = "vat_rate", nullable = false)
    private Double vatRate;

    @Column(name = "penalty_rate", nullable = false)
    private Double penaltyRate;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "active_from", nullable = false)
    private LocalDate activeFrom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
}
