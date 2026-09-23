package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByPharmacyProductIdOrderByCheckedAtAsc(Long pharmacyProductId);

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.pharmacyProduct.masterProduct.id = :masterProductId ORDER BY ph.checkedAt ASC")
    List<PriceHistory> findByMasterProductIdOrderByCheckedAtAsc(@Param("masterProductId") Long masterProductId);
}
