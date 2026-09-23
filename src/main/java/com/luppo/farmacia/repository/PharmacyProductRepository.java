package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.PharmacyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PharmacyProductRepository extends JpaRepository<PharmacyProduct, Long> {
    List<PharmacyProduct> findByMasterProductId(Long masterProductId);

    Optional<PharmacyProduct> findByPharmacyIdAndExternalId(Long pharmacyId, String externalId);

    @Query("SELECT pp FROM PharmacyProduct pp JOIN FETCH pp.pharmacy p WHERE pp.masterProduct.id = :masterProductId")
    List<PharmacyProduct> findByMasterProductIdWithPharmacy(@Param("masterProductId") Long masterProductId);
}
