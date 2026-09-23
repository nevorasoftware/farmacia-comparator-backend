package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    Optional<Pharmacy> findByCode(String code);
}
