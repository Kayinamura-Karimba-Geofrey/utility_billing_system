package project.utility.utility_billing_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.utility.utility_billing_system.dto.PaymentRequest;
import project.utility.utility_billing_system.entity.Payment;
import project.utility.utility_billing_system.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FINANCE', 'CUSTOMER')")
    public ResponseEntity<Payment> processPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(request);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/bill/{billId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
    public ResponseEntity<List<Payment>> getPaymentsByBill(@PathVariable Long billId) {
        List<Payment> payments = paymentService.getPaymentsByBill(billId);
        return ResponseEntity.ok(payments);
    }
}
