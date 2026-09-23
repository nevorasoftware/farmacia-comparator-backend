package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.MasterProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MasterProductRepository extends JpaRepository<MasterProduct, Long> {
    
    @Query("SELECT p FROM MasterProduct p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.activeIngredient) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.healthRegistration) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MasterProduct> searchProducts(@Param("query") String query);

    @Query("SELECT DISTINCT p.name FROM MasterProduct p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.activeIngredient) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.name ASC")
    List<String> findSuggestions(@Param("query") String query);

    Optional<MasterProduct> findFirstByNameIgnoreCase(String name);

    List<MasterProduct> findByActiveIngredientIgnoreCase(String activeIngredient);
}
