package project.utility.utility_billing_system.service;

import project.utility.utility_billing_system.entity.Customer;
import java.util.List;

public interface CustomerService {
    Customer saveCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
}
