package project.utility.utility_billing_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UtilityBillingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtilityBillingSystemApplication.class, args);
	}

}
