package project.utility.utility_billing_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reading_id")
    private MeterReading reading;

    @Column(name = "bill_reference", nullable = false, unique = true)
    private String billReference;

    @Column(name = "billing_period", nullable = false)
    private String billingPeriod; // e.g. "06/2026"

    @Column(nullable = false)
    private Double consumption;

    @Column(name = "tariff_rate_used", nullable = false)
    private Double tariffRateUsed;

    @Column(name = "fixed_charge_used", nullable = false)
    private Double fixedChargeUsed;

    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount;

    @Column(name = "penalty_amount", nullable = false)
    private Double penaltyAmount;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "paid_amount", nullable = false)
    private Double paidAmount;

    @Column(name = "outstanding_balance", nullable = false)
    private Double outstandingBalance;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;
}
