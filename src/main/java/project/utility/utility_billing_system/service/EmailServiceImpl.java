package project.utility.utility_billing_system.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Override
    @Async
    public void sendBillGeneratedEmail(String toEmail, String customerName, String billingPeriod, double totalAmount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Utility Bill Generated - " + billingPeriod);

            String body = "Dear " + customerName + ",\n\n"
                    + "Your " + billingPeriod + " utility bill of "
                    + String.format("%.2f", totalAmount) + " FRW has been successfully processed.\n\n"
                    + "Please ensure payment is made before the due date to avoid penalties.\n\n"
                    + "Thank you for using our utility services.\n\n"
                    + "Regards,\n"
                    + "Utility Billing System";

            helper.setText(body, false);
            mailSender.send(message);
            logger.info("Bill generated email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send bill generated email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPaymentConfirmationEmail(String toEmail, String customerName, String billingPeriod, double totalAmount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Payment Confirmed - " + billingPeriod + " Bill Fully Paid");

            String body = "Dear " + customerName + ",\n\n"
                    + "Your " + billingPeriod + " utility bill of "
                    + String.format("%.2f", totalAmount) + " FRW has been fully paid.\n\n"
                    + "Your account is now up to date. Thank you for your prompt payment.\n\n"
                    + "Regards,\n"
                    + "Utility Billing System";

            helper.setText(body, false);
            mailSender.send(message);
            logger.info("Payment confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send payment confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }
}
