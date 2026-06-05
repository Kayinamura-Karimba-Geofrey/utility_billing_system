package project.utility.utility_billing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.utility.utility_billing_system.entity.Customer;
import project.utility.utility_billing_system.exception.DuplicateResourceException;
import project.utility.utility_billing_system.exception.ResourceNotFoundException;
import project.utility.utility_billing_system.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer saveCustomer(Customer customer) {
        if (customerRepository.existsByNationalId(customer.getNationalId())) {
            throw new DuplicateResourceException("Customer with National ID " + customer.getNationalId() + " already exists.");
        }
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
