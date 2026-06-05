package project.utility.utility_billing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.utility.utility_billing_system.entity.MeterType;
import project.utility.utility_billing_system.entity.Tariff;
import project.utility.utility_billing_system.entity.UserStatus;
import project.utility.utility_billing_system.exception.ResourceNotFoundException;
import project.utility.utility_billing_system.repository.TariffRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TariffServiceImpl implements TariffService {

    @Autowired
    private TariffRepository tariffRepository;

    @Override
    public Tariff configureTariff(Tariff tariff) {
        // Find latest version of tariff for the given meter type
        Optional<Tariff> latestTariff = tariffRepository.findFirstByMeterTypeOrderByVersionDesc(tariff.getMeterType());
        int nextVersion = latestTariff.map(value -> value.getVersion() + 1).orElse(1);

        tariff.setVersion(nextVersion);
        tariff.setStatus(UserStatus.ACTIVE);
        if (tariff.getActiveFrom() == null) {
            tariff.setActiveFrom(LocalDate.now());
        }
        return tariffRepository.save(tariff);
    }

    @Override
    public Tariff getActiveTariffForDate(MeterType meterType, LocalDate date) {
        List<Tariff> activeTariffs = tariffRepository.findActiveTariffsForDate(meterType, date);
        if (activeTariffs.isEmpty()) {
            throw new ResourceNotFoundException("No active tariff configured for meter type " + meterType + " on date " + date);
        }
        return activeTariffs.get(0); // The query sorts by activeFrom DESC, version DESC
    }

    @Override
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }
}
