package project.utility.utility_billing_system.service;

import project.utility.utility_billing_system.entity.Bill;
import java.util.List;

public interface BillService {
    Bill generateBillFromReading(Long readingId);
    Bill approveBill(Long billId);
    List<Bill> getBillsByCustomer(Long customerId);
    Bill getBillByReference(String billReference);
    List<Bill> getAllBills();
}
