package project.utility.utility_billing_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.utility.utility_billing_system.entity.Bill;
import project.utility.utility_billing_system.service.BillService;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @PostMapping("/generate/{readingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'OPERATOR')")
    public ResponseEntity<Bill> generateBill(@PathVariable Long readingId) {
        Bill bill = billService.generateBillFromReading(readingId);
        return new ResponseEntity<>(bill, HttpStatus.CREATED);
    }

    @PostMapping("/{billId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<Bill> approveBill(@PathVariable Long billId) {
        Bill bill = billService.approveBill(billId);
        return ResponseEntity.ok(bill);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
    public ResponseEntity<List<Bill>> getBillsByCustomer(@PathVariable Long customerId) {
        List<Bill> bills = billService.getBillsByCustomer(customerId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'CUSTOMER')")
    public ResponseEntity<Bill> getBillByReference(@PathVariable String reference) {
        Bill bill = billService.getBillByReference(reference);
        return ResponseEntity.ok(bill);
    }
}
