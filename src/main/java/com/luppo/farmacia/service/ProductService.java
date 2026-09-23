package com.luppo.farmacia.service;

import com.luppo.farmacia.dto.*;
import com.luppo.farmacia.entity.MasterProduct;
import com.luppo.farmacia.entity.PharmacyProduct;
import com.luppo.farmacia.entity.SrsPrice;
import com.luppo.farmacia.exception.ResourceNotFoundException;
import com.luppo.farmacia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final MasterProductRepository masterProductRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final SrsProductRepository srsProductRepository;
    private final SrsPriceRepository srsPriceRepository;
    private final ComparisonService comparisonService;
    private final OnDemandScraperService onDemandScraperService;

    @Transactional
    public List<ProductSearchDto> searchProducts(String query) {
        List<MasterProduct> products;
        if (query == null || query.trim().isEmpty()) {
            products = masterProductRepository.findAll();
        } else {
            String cleanQuery = query.trim();
            products = masterProductRepository.searchProducts(cleanQuery);

            // 1. Si no existe en la base de datos local, buscamos en tiempo real con scraping y normalizamos con Gemini
            if (products.isEmpty() && cleanQuery.length() >= 3) {
                int ingested = onDemandScraperService.scrapeAndIngest(cleanQuery);
                if (ingested > 0) {
                    products = masterProductRepository.searchProducts(cleanQuery);
                }
            }

            // 2. Si todavía no hay resultados directos, intentar búsqueda tokenizada (palabra por palabra)
            if (products.isEmpty() && cleanQuery.contains(" ")) {
                String[] words = cleanQuery.split("\\s+");
                Set<Long> seenIds = new HashSet<>();
                products = new ArrayList<>();
                for (String w : words) {
                    if (w.trim().length() >= 3) {
                        List<MasterProduct> tokenMatches = masterProductRepository.searchProducts(w.trim());
                        for (MasterProduct p : tokenMatches) {
                            if (seenIds.add(p.getId())) {
                                products.add(p);
                            }
                        }
                    }
                }
            }
        }

        return products.stream().map(this::mapToSearchDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getSuggestions(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }
        return masterProductRepository.findSuggestions(query.trim()).stream()
                .limit(8)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductComparisonDto getProductComparison(Long id) {
        MasterProduct product = masterProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento no encontrado con ID: " + id));
        return comparisonService.buildComparison(product);
    }

    @Transactional(readOnly = true)
    public MasterProduct getProductById(Long id) {
        return masterProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento no encontrado con ID: " + id));
    }

    private ProductSearchDto mapToSearchDto(MasterProduct p) {
        List<PharmacyProduct> pharmacyProducts = pharmacyProductRepository.findByMasterProductId(p.getId());

        BigDecimal min = pharmacyProducts.stream()
                .map(pp -> pp.getCurrentOfferPrice() != null ? pp.getCurrentOfferPrice() : pp.getCurrentPrice())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        BigDecimal max = pharmacyProducts.stream()
                .map(PharmacyProduct::getCurrentPrice)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        BigDecimal pvmp = srsProductRepository.findByMasterProductId(p.getId())
                .flatMap(s -> srsPriceRepository.findTopBySrsProductIdOrderByCreatedAtDesc(s.getId()))
                .map(SrsPrice::getPvmp)
                .orElse(null);

        return ProductSearchDto.builder()
                .id(p.getId())
                .name(p.getName())
                .activeIngredient(p.getActiveIngredient())
                .concentration(p.getConcentration())
                .pharmaceuticalForm(p.getPharmaceuticalForm())
                .brand(p.getBrand())
                .laboratory(p.getLaboratory())
                .healthRegistration(p.getHealthRegistration())
                .presentation(p.getPresentation())
                .minPrice(min)
                .maxPrice(max)
                .pvmpSrs(pvmp)
                .availablePharmaciesCount(pharmacyProducts.size())
                .build();
    }
}
