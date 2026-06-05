package project.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank(message = "Bill reference is required")
    private String billReference;

    @NotNull(message = "Amount paid is required")
    @Min(value = 0, message = "Amount paid must be greater than zero")
    private Double amountPaid;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
