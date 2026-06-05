package project.utility.utility_billing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.utility.utility_billing_system.dto.ReadingRequest;
import project.utility.utility_billing_system.entity.*;
import project.utility.utility_billing_system.exception.InvalidActionException;
import project.utility.utility_billing_system.exception.ResourceNotFoundException;
import project.utility.utility_billing_system.repository.MeterReadingRepository;
import project.utility.utility_billing_system.repository.MeterRepository;
import project.utility.utility_billing_system.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public MeterReading captureReading(ReadingRequest request, String operatorEmail) {
        User operator = userRepository.findByEmail(operatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + operatorEmail));

        Meter meter = meterRepository.findByMeterNumber(request.getMeterNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with number: " + request.getMeterNumber()));

        // Rule: Meter must be active
        if (meter.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidActionException("Cannot capture reading: Meter is INACTIVE.");
        }

        // Rule: Inactive customers cannot receive bills
        if (meter.getCustomer().getStatus() != UserStatus.ACTIVE) {
            throw new InvalidActionException("Cannot capture reading: Customer is INACTIVE.");
        }

        LocalDate readingDate = request.getReadingDate();
        int month = readingDate.getMonthValue();
        int year = readingDate.getYear();

        // Rule: Only one reading per meter per Month/Year
        if (meterReadingRepository.existsByMeterIdAndBillingMonthAndBillingYear(meter.getId(), month, year)) {
            throw new InvalidActionException("A reading has already been captured for this meter in " + month + "/" + year);
        }

        // Retrieve previous reading
        Optional<MeterReading> latestReadingOpt = meterReadingRepository.findFirstByMeterIdOrderByReadingDateDesc(meter.getId());
        double previousReading = latestReadingOpt.map(MeterReading::getCurrentReading).orElse(0.0);

        // Rule: Current reading must be greater than previous reading
        if (request.getCurrentReading() <= previousReading) {
            throw new InvalidActionException("Current reading (" + request.getCurrentReading() + 
                    ") must be greater than the previous reading (" + previousReading + ").");
        }

        MeterReading reading = MeterReading.builder()
                .meter(meter)
                .previousReading(previousReading)
                .currentReading(request.getCurrentReading())
                .readingDate(readingDate)
                .billingMonth(month)
                .billingYear(year)
                .capturedBy(operator)
                .build();

        return meterReadingRepository.save(reading);
    }

    @Override
    public List<MeterReading> getAllReadings() {
        return meterReadingRepository.findAll();
    }
}
