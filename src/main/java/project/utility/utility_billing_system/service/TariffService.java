package project.utility.utility_billing_system.service;

import project.utility.utility_billing_system.entity.MeterType;
import project.utility.utility_billing_system.entity.Tariff;

import java.time.LocalDate;
import java.util.List;

public interface TariffService {
    Tariff configureTariff(Tariff tariff);
    Tariff getActiveTariffForDate(MeterType meterType, LocalDate date);
    List<Tariff> getAllTariffs();
}
