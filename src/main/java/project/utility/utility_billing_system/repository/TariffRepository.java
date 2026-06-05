package project.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.utility.utility_billing_system.entity.MeterType;
import project.utility.utility_billing_system.entity.Tariff;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

    @Query("SELECT t FROM Tariff t WHERE t.meterType = :meterType AND t.status = 'ACTIVE' AND t.activeFrom <= :date ORDER BY t.activeFrom DESC, t.version DESC")
    List<Tariff> findActiveTariffsForDate(@Param("meterType") MeterType meterType, @Param("date") LocalDate date);

    Optional<Tariff> findFirstByMeterTypeOrderByVersionDesc(MeterType meterType);
}
