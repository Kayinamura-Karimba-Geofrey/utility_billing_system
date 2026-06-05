package project.utility.utility_billing_system.service;

import project.utility.utility_billing_system.entity.Meter;
import java.util.List;

public interface MeterService {
    Meter saveMeter(Long customerId, Meter meter);
    List<Meter> getMetersByCustomerId(Long customerId);
    Meter getMeterByNumber(String meterNumber);
}
