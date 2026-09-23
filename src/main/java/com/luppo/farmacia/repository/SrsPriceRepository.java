package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.SrsPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SrsPriceRepository extends JpaRepository<SrsPrice, Long> {
    Optional<SrsPrice> findTopBySrsProductIdOrderByCreatedAtDesc(Long srsProductId);
}
