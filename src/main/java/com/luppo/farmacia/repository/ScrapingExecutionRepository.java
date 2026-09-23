package com.luppo.farmacia.repository;

import com.luppo.farmacia.entity.ScrapingExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ScrapingExecutionRepository extends JpaRepository<ScrapingExecution, Long> {
    List<ScrapingExecution> findTop20ByOrderByStartedAtDesc();

    @Query("SELECT se FROM ScrapingExecution se WHERE se.id IN (SELECT MAX(s.id) FROM ScrapingExecution s GROUP BY s.pharmacy.id)")
    List<ScrapingExecution> findLatestExecutionsPerPharmacy();
}
