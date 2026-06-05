package project.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.utility.utility_billing_system.entity.MeterReading;

import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    Optional<MeterReading> findFirstByMeterIdOrderByReadingDateDesc(Long meterId);
    boolean existsByMeterIdAndBillingMonthAndBillingYear(Long meterId, Integer billingMonth, Integer billingYear);
}
