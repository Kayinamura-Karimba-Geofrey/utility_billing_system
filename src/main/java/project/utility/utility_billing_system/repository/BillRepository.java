package project.utility.utility_billing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.utility.utility_billing_system.entity.Bill;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillReference(String billReference);
    List<Bill> findByMeterCustomerId(Long customerId);
    boolean existsByBillReference(String billReference);
}
