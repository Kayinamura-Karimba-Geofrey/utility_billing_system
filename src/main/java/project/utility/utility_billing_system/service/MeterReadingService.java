package project.utility.utility_billing_system.service;

import project.utility.utility_billing_system.dto.ReadingRequest;
import project.utility.utility_billing_system.entity.MeterReading;

import java.util.List;

public interface MeterReadingService {
    MeterReading captureReading(ReadingRequest request, String operatorEmail);
    List<MeterReading> getAllReadings();
}
