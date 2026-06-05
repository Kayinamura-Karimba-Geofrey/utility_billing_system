package project.utility.utility_billing_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "meters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "meter_number", nullable = false, unique = true)
    private String meterNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false)
    private MeterType meterType;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
}
