package project.utility.utility_billing_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.utility.utility_billing_system.dto.PaymentRequest;
import project.utility.utility_billing_system.entity.Bill;
import project.utility.utility_billing_system.entity.BillStatus;
import project.utility.utility_billing_system.entity.Payment;
import project.utility.utility_billing_system.exception.InvalidActionException;
import project.utility.utility_billing_system.exception.ResourceNotFoundException;
import project.utility.utility_billing_system.repository.BillRepository;
import project.utility.utility_billing_system.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Override
    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Bill bill = billRepository.findByBillReference(request.getBillReference())
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with reference: " + request.getBillReference()));

        // Check if bill is approved
        if (bill.getStatus() == BillStatus.PENDING_APPROVAL) {
            throw new InvalidActionException("Cannot pay bill that is pending approval.");
        }

        // Check if already paid
        if (bill.getStatus() == BillStatus.PAID || bill.getOutstandingBalance() <= 0) {
            throw new InvalidActionException("Bill is already fully paid.");
        }

        double outstanding = bill.getOutstandingBalance();
        double paymentAmount = request.getAmountPaid();

        if (paymentAmount <= 0) {
            throw new InvalidActionException("Payment amount must be greater than zero.");
        }

        if (paymentAmount > outstanding) {
            throw new InvalidActionException("Payment amount (" + paymentAmount + ") exceeds outstanding balance (" + outstanding + ").");
        }

        // Update Bill details
        double newPaidAmount = bill.getPaidAmount() + paymentAmount;
        double newOutstanding = outstanding - paymentAmount;

        bill.setPaidAmount(newPaidAmount);
        bill.setOutstandingBalance(newOutstanding);

        if (newOutstanding <= 0.001) { // Floating point safety margin
            bill.setOutstandingBalance(0.0);
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }

        billRepository.save(bill);

        // Record Payment
        Payment payment = Payment.builder()
                .bill(bill)
                .billReference(bill.getBillReference())
                .amountPaid(paymentAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentDate(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentsByBill(Long billId) {
        return paymentRepository.findByBillId(billId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
