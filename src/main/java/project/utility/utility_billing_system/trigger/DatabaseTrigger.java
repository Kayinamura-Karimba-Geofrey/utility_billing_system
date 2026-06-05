package project.utility.utility_billing_system.trigger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTrigger implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        String createFunctionSql = 
            "CREATE OR REPLACE FUNCTION bill_trigger_function() " +
            "RETURNS TRIGGER AS $$ " +
            "DECLARE " +
            "    v_customer_name VARCHAR; " +
            "    v_email VARCHAR; " +
            "    v_phone_number VARCHAR; " +
            "    v_notif_count INT; " +
            "    v_message TEXT; " +
            "BEGIN " +
            "    SELECT c.full_names, c.email, c.phone_number " +
            "    INTO v_customer_name, v_email, v_phone_number " +
            "    FROM meters m " +
            "    JOIN customers c ON m.customer_id = c.id " +
            "    WHERE m.id = NEW.meter_id; " +
            "    " +
            "    IF TG_OP = 'INSERT' THEN " +
            "        v_message := format('Dear %s,\\nYour %s utility bill of %s FRW has been successfully processed.', " +
            "                            v_customer_name, NEW.billing_period, NEW.total_amount); " +
            "        INSERT INTO notifications (customer_name, email, phone_number, message, sent_at, trigger_event, bill_id) " +
            "        VALUES (v_customer_name, v_email, v_phone_number, v_message, NOW(), 'BILL_GENERATED', NEW.id); " +
            "    ELSIF TG_OP = 'UPDATE' AND NEW.status = 'PAID' THEN " +
            "        SELECT COUNT(*) INTO v_notif_count FROM notifications WHERE trigger_event = 'BILL_PAID' AND bill_id = NEW.id; " +
            "        IF v_notif_count = 0 THEN " +
            "            v_message := format('Dear %s,\\nYour payment for the %s utility bill of %s FRW has been successfully processed. Current Balance: 0.0 FRW.', " +
            "                                v_customer_name, NEW.billing_period, NEW.total_amount); " +
            "            INSERT INTO notifications (customer_name, email, phone_number, message, sent_at, trigger_event, bill_id) " +
            "            VALUES (v_customer_name, v_email, v_phone_number, v_message, NOW(), 'BILL_PAID', NEW.id); " +
            "        END IF; " +
            "    END IF; " +
            "    RETURN NEW; " +
            "END; " +
            "$$ LANGUAGE plpgsql;";

        String createTriggerSql = 
            "DROP TRIGGER IF EXISTS bill_trigger ON bills; " +
            "CREATE TRIGGER bill_trigger " +
            "AFTER INSERT OR UPDATE ON bills " +
            "FOR EACH ROW " +
            "EXECUTE FUNCTION bill_trigger_function();";
            
        jdbcTemplate.execute(createFunctionSql);
        jdbcTemplate.execute(createTriggerSql);
        System.out.println("PostgreSQL Database Trigger successfully registered on table 'bills'");
    }
}
