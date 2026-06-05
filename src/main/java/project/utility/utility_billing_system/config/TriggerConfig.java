package project.utility.utility_billing_system.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TriggerConfig implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Trigger setup moved to DatabaseTrigger.java for PostgreSQL
    }
}
