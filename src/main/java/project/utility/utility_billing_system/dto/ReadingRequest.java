package project.utility.utility_billing_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReadingRequest {

    @NotBlank(message = "Meter number is required")
    private String meterNumber;

    @NotNull(message = "Current reading is required")
    @Min(value = 0, message = "Reading cannot be negative")
    private Double currentReading;

    @NotNull(message = "Reading date is required")
    private LocalDate readingDate;
}
