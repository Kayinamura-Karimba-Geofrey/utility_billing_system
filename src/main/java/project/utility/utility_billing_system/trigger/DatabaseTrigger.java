package project.utility.utility_billing_system.trigger;

import org.h2.api.Trigger;

import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName,
                     String tableName, boolean before, int type) throws SQLException {
        // No-op
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if (newRow == null) return;

        // newRow[0] is always the primary key (id) in H2
        Long billId = ((Number) newRow[0]).longValue();
        boolean isInsert = (oldRow == null);

        // Query full bill + customer info from the DB (always reliable)
        String selectSql =
                "SELECT b.billing_period, b.total_amount, b.status, " +
                "c.full_names, c.email, c.phone_number " +
                "FROM bills b " +
                "JOIN meters m ON b.meter_id = m.id " +
                "JOIN customers c ON m.customer_id = c.id " +
                "WHERE b.id = ?";

        String billingPeriod = "", status = "", customerName = "", email = "", phoneNumber = "";
        double totalAmount = 0.0;

        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return; // row not yet visible (shouldn't happen AFTER trigger)
                billingPeriod = rs.getString("billing_period");
                totalAmount   = rs.getDouble("total_amount");
                status        = rs.getString("status");
                customerName  = rs.getString("full_names");
                email         = rs.getString("email");
                phoneNumber   = rs.getString("phone_number");
            }
        }

        if (isInsert) {
            // Bill generation notification
            String message = String.format(
                    "Dear %s,\nYour %s utility bill of %.1f FRW has been successfully processed.",
                    customerName, billingPeriod, totalAmount);
            insertNotification(conn, customerName, email, phoneNumber, message, "BILL_GENERATED", billId);

        } else if ("PAID".equals(status)) {
            // Only insert a BILL_PAID notification once per bill
            if (!billPaidNotificationExists(conn, billId)) {
                String message = String.format(
                        "Dear %s,\nYour payment for the %s utility bill of %.1f FRW has been successfully processed. Current Balance: 0.0 FRW.",
                        customerName, billingPeriod, totalAmount);
                insertNotification(conn, customerName, email, phoneNumber, message, "BILL_PAID", billId);
            }
        }
    }

    private boolean billPaidNotificationExists(Connection conn, Long billId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE trigger_event = 'BILL_PAID' AND bill_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void insertNotification(Connection conn, String customerName, String email,
                                    String phoneNumber, String message,
                                    String event, Long billId) throws SQLException {
        String sql = "INSERT INTO notifications " +
                "(customer_name, email, phone_number, message, sent_at, trigger_event, bill_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerName);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);
            ps.setString(4, message);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, event);
            ps.setLong(7, billId);
            ps.executeUpdate();
        }
    }

    @Override
    public void close() throws SQLException {
        // No-op
    }

    @Override
    public void remove() throws SQLException {
        // No-op
    }
}
