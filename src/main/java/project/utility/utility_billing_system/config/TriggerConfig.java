package project.utility.utility_billing_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TriggerConfig implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Create the H2 database trigger on the bills table
        String createTriggerSql = "CREATE TRIGGER IF NOT EXISTS bill_trigger " +
                "AFTER INSERT, UPDATE ON bills " +
                "FOR EACH ROW " +
                "CALL \"project.utility.utility_billing_system.trigger.DatabaseTrigger\"";
        
        jdbcTemplate.execute(createTriggerSql);
        System.out.println("H2 Database Trigger successfully registered on table 'bills'");
    }
}
