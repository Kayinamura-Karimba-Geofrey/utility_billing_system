package project.utility.utility_billing_system.service;

public interface EmailService {
    void sendBillGeneratedEmail(String toEmail, String customerName, String billingPeriod, double totalAmount);
    void sendPaymentConfirmationEmail(String toEmail, String customerName, String billingPeriod, double totalAmount);
}
