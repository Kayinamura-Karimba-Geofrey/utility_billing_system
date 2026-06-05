package project.utility.utility_billing_system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.utility.utility_billing_system.dto.PaymentRequest;
import project.utility.utility_billing_system.dto.ReadingRequest;
import project.utility.utility_billing_system.entity.*;
import project.utility.utility_billing_system.exception.DuplicateResourceException;
import project.utility.utility_billing_system.exception.InvalidActionException;
import project.utility.utility_billing_system.repository.*;
import project.utility.utility_billing_system.service.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UtilityBillingSystemApplicationTests {

    @Autowired private UserRepository userRepository;
    @Autowired private CustomerService customerService;
    @Autowired private MeterService meterService;
    @Autowired private TariffService tariffService;
    @Autowired private MeterReadingService meterReadingService;
    @Autowired private BillService billService;
    @Autowired private BillRepository billRepository;
    @Autowired private PaymentService paymentService;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private MeterReadingRepository meterReadingRepository;
    @Autowired private MeterRepository meterRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private TariffRepository tariffRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanUp() {
        // Delete in FK-safe order
        paymentRepository.deleteAll();
        billRepository.deleteAll();
        notificationRepository.deleteAll();
        meterReadingRepository.deleteAll();
        meterRepository.deleteAll();
        customerRepository.deleteAll();
        tariffRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFullUtilityBillingLifecycle() {
        // ── 1. User Management ────────────────────────────────────────────────
        User operator = User.builder()
                .fullNames("Test Operator")
                .email("operator@wasac.gov.rw")
                .phoneNumber("0780000001")
                .password(passwordEncoder.encode("password123"))
                .status(UserStatus.ACTIVE)
                .role(Role.ROLE_OPERATOR)
                .build();
        userRepository.save(operator);
        assertTrue(userRepository.existsByEmail("operator@wasac.gov.rw"));

        // ── 2. Customer Registration & Duplicate Prevention ───────────────────
        Customer customer = Customer.builder()
                .fullNames("John Doe")
                .nationalId("1199080070000055")
                .email("john.doe@gmail.com")
                .phoneNumber("0788123456")
                .address("Kigali, Rwanda")
                .status(UserStatus.ACTIVE)
                .build();
        Customer savedCustomer = customerService.saveCustomer(customer);
        assertNotNull(savedCustomer.getId());

        Customer duplicateCustomer = Customer.builder()
                .fullNames("John Copy")
                .nationalId("1199080070000055")
                .email("john.copy@gmail.com")
                .phoneNumber("0788654321")
                .address("Kigali, Rwanda")
                .status(UserStatus.ACTIVE)
                .build();
        assertThrows(DuplicateResourceException.class,
                () -> customerService.saveCustomer(duplicateCustomer));

        // ── 3. Meter Registration ─────────────────────────────────────────────
        Meter waterMeter = Meter.builder()
                .meterNumber("WAT-100200")
                .meterType(MeterType.WATER)
                .installationDate(LocalDate.now().minusMonths(6))
                .status(UserStatus.ACTIVE)
                .build();
        Meter savedMeter = meterService.saveMeter(savedCustomer.getId(), waterMeter);
        assertNotNull(savedMeter.getId());

        Meter duplicateMeter = Meter.builder()
                .meterNumber("WAT-100200")
                .meterType(MeterType.WATER)
                .installationDate(LocalDate.now())
                .status(UserStatus.ACTIVE)
                .build();
        assertThrows(DuplicateResourceException.class,
                () -> meterService.saveMeter(savedCustomer.getId(), duplicateMeter));

        // ── 4. Tariff Versioning ──────────────────────────────────────────────
        Tariff waterTariffV1 = Tariff.builder()
                .meterType(MeterType.WATER)
                .tariffName("WASAC Water Standard")
                .ratePerUnit(350.0)
                .fixedServiceCharge(1500.0)
                .vatRate(0.18)
                .penaltyRate(0.10)
                .activeFrom(LocalDate.now().minusMonths(1))
                .build();
        Tariff savedV1 = tariffService.configureTariff(waterTariffV1);
        assertEquals(1, savedV1.getVersion());

        Tariff waterTariffV2 = Tariff.builder()
                .meterType(MeterType.WATER)
                .tariffName("WASAC Water Updated")
                .ratePerUnit(400.0)
                .fixedServiceCharge(1600.0)
                .vatRate(0.18)
                .penaltyRate(0.10)
                .activeFrom(LocalDate.now().plusDays(10)) // future — should NOT apply today
                .build();
        Tariff savedV2 = tariffService.configureTariff(waterTariffV2);
        assertEquals(2, savedV2.getVersion());

        // Active tariff today must still be V1
        Tariff activeToday = tariffService.getActiveTariffForDate(MeterType.WATER, LocalDate.now());
        assertEquals(1, activeToday.getVersion());
        assertEquals(350.0, activeToday.getRatePerUnit());

        // ── 5. Meter Reading Capture ──────────────────────────────────────────
        ReadingRequest readingRequest = new ReadingRequest();
        readingRequest.setMeterNumber("WAT-100200");
        readingRequest.setCurrentReading(100.0);
        readingRequest.setReadingDate(LocalDate.now());

        MeterReading reading = meterReadingService.captureReading(readingRequest, "operator@wasac.gov.rw");
        assertEquals(0.0, reading.getPreviousReading());
        assertEquals(100.0, reading.getCurrentReading());

        // Only one reading per month/year per meter
        assertThrows(InvalidActionException.class,
                () -> meterReadingService.captureReading(readingRequest, "operator@wasac.gov.rw"));

        // Current reading must be greater than previous
        ReadingRequest lowReading = new ReadingRequest();
        lowReading.setMeterNumber("WAT-100200");
        lowReading.setCurrentReading(90.0);
        lowReading.setReadingDate(LocalDate.now().plusMonths(1));
        assertThrows(InvalidActionException.class,
                () -> meterReadingService.captureReading(lowReading, "operator@wasac.gov.rw"));

        // ── 6. Bill Generation ────────────────────────────────────────────────
        // consumption = 100 - 0 = 100 units
        // base = (100 * 350) + 1500 = 36500
        // tax  = 36500 * 0.18 = 6570
        // total = 36500 + 6570 = 43070
        Bill bill = billService.generateBillFromReading(reading.getId());
        assertEquals(BillStatus.PENDING_APPROVAL, bill.getStatus());
        assertEquals(100.0, bill.getConsumption());
        assertEquals(43070.0, bill.getTotalAmount());
        assertEquals(43070.0, bill.getOutstandingBalance());

        // BILL_GENERATED notification must have been inserted by DB trigger
        List<Notification> notifications = notificationRepository.findAll();
        assertFalse(notifications.isEmpty(), "No notifications found after bill generation");
        Notification genNotif = notifications.stream()
                .filter(n -> "BILL_GENERATED".equals(n.getTriggerEvent()))
                .findFirst().orElse(null);
        assertNotNull(genNotif, "BILL_GENERATED notification is null");
        assertTrue(genNotif.getMessage().contains("John Doe"));
        assertTrue(genNotif.getMessage().contains("43070.0 FRW"));

        // ── 7. Bill Approval ──────────────────────────────────────────────────
        Bill approvedBill = billService.approveBill(bill.getId());
        assertEquals(BillStatus.APPROVED, approvedBill.getStatus());

        // Cannot re-approve
        assertThrows(InvalidActionException.class, () -> billService.approveBill(bill.getId()));

        // ── 8. Payments (Partial then Full) ───────────────────────────────────
        PaymentRequest partialPayment = new PaymentRequest();
        partialPayment.setBillReference(approvedBill.getBillReference());
        partialPayment.setAmountPaid(20000.0);
        partialPayment.setPaymentMethod("Mobile Money");

        Payment p1 = paymentService.processPayment(partialPayment);
        assertEquals(20000.0, p1.getAmountPaid());

        Bill partPaid = billRepository.findById(approvedBill.getId()).orElseThrow();
        assertEquals(BillStatus.PARTIALLY_PAID, partPaid.getStatus());
        assertEquals(23070.0, partPaid.getOutstandingBalance());

        PaymentRequest fullPayment = new PaymentRequest();
        fullPayment.setBillReference(approvedBill.getBillReference());
        fullPayment.setAmountPaid(23070.0);
        fullPayment.setPaymentMethod("Bank Transfer");

        Payment p2 = paymentService.processPayment(fullPayment);
        assertEquals(23070.0, p2.getAmountPaid());

        Bill fullyPaid = billRepository.findById(approvedBill.getId()).orElseThrow();
        assertEquals(BillStatus.PAID, fullyPaid.getStatus());
        assertEquals(0.0, fullyPaid.getOutstandingBalance());

        // BILL_PAID notification must have been inserted by DB trigger
        List<Notification> allNotifications = notificationRepository.findAll();
        Notification paidNotif = allNotifications.stream()
                .filter(n -> "BILL_PAID".equals(n.getTriggerEvent()))
                .findFirst().orElse(null);
        assertNotNull(paidNotif, "BILL_PAID notification is null — trigger did not fire for PAID status");
        assertTrue(paidNotif.getMessage().contains("successfully processed"));
    }
}
