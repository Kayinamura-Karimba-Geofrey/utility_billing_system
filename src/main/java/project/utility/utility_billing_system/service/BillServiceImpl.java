package project.utility.utility_billing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.utility.utility_billing_system.entity.*;
import project.utility.utility_billing_system.exception.InvalidActionException;
import project.utility.utility_billing_system.exception.ResourceNotFoundException;
import project.utility.utility_billing_system.repository.BillRepository;
import project.utility.utility_billing_system.repository.MeterReadingRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @Autowired
    private TariffService tariffService;

    @Override
    @Transactional
    public Bill generateBillFromReading(Long readingId) {
        MeterReading reading = meterReadingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with id: " + readingId));

        // Check if a bill is already generated for this reading
        List<Bill> allBills = billRepository.findAll();
        boolean billExists = allBills.stream()
                .anyMatch(b -> b.getReading() != null && b.getReading().getId().equals(readingId));
        if (billExists) {
            throw new InvalidActionException("A bill has already been generated for this meter reading.");
        }

        Meter meter = reading.getMeter();
        
        // Fetch active tariff based on the meter type and reading date
        Tariff activeTariff = tariffService.getActiveTariffForDate(meter.getMeterType(), reading.getReadingDate());

        double consumption = reading.getCurrentReading() - reading.getPreviousReading();
        double baseAmount = (consumption * activeTariff.getRatePerUnit()) + activeTariff.getFixedServiceCharge();
        double taxAmount = baseAmount * activeTariff.getVatRate();
        double totalAmount = baseAmount + taxAmount;

        String period = String.format("%02d/%d", reading.getBillingMonth(), reading.getBillingYear());
        String reference = "BILL-" + meter.getMeterNumber() + "-" + reading.getBillingYear() + String.format("%02d", reading.getBillingMonth());

        // Ensure reference uniqueness (e.g. if we delete/recreate)
        if (billRepository.existsByBillReference(reference)) {
            reference += "-" + readingId;
        }

        Bill bill = Bill.builder()
                .meter(meter)
                .reading(reading)
                .billReference(reference)
                .billingPeriod(period)
                .consumption(consumption)
                .tariffRateUsed(activeTariff.getRatePerUnit())
                .fixedChargeUsed(activeTariff.getFixedServiceCharge())
                .taxAmount(taxAmount)
                .penaltyAmount(0.0)
                .totalAmount(totalAmount)
                .paidAmount(0.0)
                .outstandingBalance(totalAmount)
                .dueDate(reading.getReadingDate().plusDays(15))
                .status(BillStatus.PENDING_APPROVAL)
                .build();

        return billRepository.save(bill);
    }

    @Override
    @Transactional
    public Bill approveBill(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));

        if (bill.getStatus() != BillStatus.PENDING_APPROVAL) {
            throw new InvalidActionException("Bill cannot be approved. Current status: " + bill.getStatus());
        }

        bill.setStatus(BillStatus.APPROVED);
        return billRepository.save(bill);
    }

    @Override
    public List<Bill> getBillsByCustomer(Long customerId) {
        return billRepository.findByMeterCustomerId(customerId);
    }

    @Override
    public Bill getBillByReference(String billReference) {
        return billRepository.findByBillReference(billReference)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with reference: " + billReference));
    }

    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
}
