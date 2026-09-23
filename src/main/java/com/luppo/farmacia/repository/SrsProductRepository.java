package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.SrsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SrsProductRepository extends JpaRepository<SrsProduct, Long> {
    Optional<SrsProduct> findByMasterProductId(Long masterProductId);
    Optional<SrsProduct> findByHealthRegistration(String healthRegistration);
}
