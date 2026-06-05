package project.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.utility.utility_billing_system.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
