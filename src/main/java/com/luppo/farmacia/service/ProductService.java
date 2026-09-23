package com.luppo.farmacia.service;

import com.luppo.farmacia.dto.*;
import com.luppo.farmacia.entity.MasterProduct;
import com.luppo.farmacia.entity.PharmacyProduct;
import com.luppo.farmacia.entity.SrsPrice;
import com.luppo.farmacia.exception.ResourceNotFoundException;
import com.luppo.farmacia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final MasterProductRepository masterProductRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final SrsProductRepository srsProductRepository;
    private final SrsPriceRepository srsPriceRepository;
    private final ComparisonService comparisonService;

    @Transactional(readOnly = true)
    public List<ProductSearchDto> searchProducts(String query) {
        List<MasterProduct> products;
        if (query == null || query.trim().isEmpty()) {
            products = masterProductRepository.findAll();
        } else {
            products = masterProductRepository.searchProducts(query.trim());
        }

        return products.stream().map(this::mapToSearchDto).collect(Collectors.toList());
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
