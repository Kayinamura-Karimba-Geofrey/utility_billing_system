package project.utility.utility_billing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.utility.utility_billing_system.entity.Customer;
import project.utility.utility_billing_system.entity.Meter;
import project.utility.utility_billing_system.exception.DuplicateResourceException;
import project.utility.utility_billing_system.exception.ResourceNotFoundException;
import project.utility.utility_billing_system.repository.CustomerRepository;
import project.utility.utility_billing_system.repository.MeterRepository;

import java.util.List;

@Service
public class MeterServiceImpl implements MeterService {

    @Autowired
    private MeterRepository meterRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Meter saveMeter(Long customerId, Meter meter) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        if (meterRepository.existsByMeterNumber(meter.getMeterNumber())) {
            throw new DuplicateResourceException("Meter with number " + meter.getMeterNumber() + " already exists.");
        }

        meter.setCustomer(customer);
        return meterRepository.save(meter);
    }

    @Override
    public List<Meter> getMetersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return meterRepository.findByCustomerId(customerId);
    }

    @Override
    public Meter getMeterByNumber(String meterNumber) {
        return meterRepository.findByMeterNumber(meterNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Meter not found with meter number: " + meterNumber));
    }
}
