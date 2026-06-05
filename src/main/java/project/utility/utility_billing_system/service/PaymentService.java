package project.utility.utility_billing_system.service;

import project.utility.utility_billing_system.dto.PaymentRequest;
import project.utility.utility_billing_system.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment processPayment(PaymentRequest request);
    List<Payment> getPaymentsByBill(Long billId);
    List<Payment> getAllPayments();
}
