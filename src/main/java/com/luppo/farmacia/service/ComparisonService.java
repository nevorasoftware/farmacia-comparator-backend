package com.luppo.farmacia.service;

import com.luppo.farmacia.dto.*;
import com.luppo.farmacia.entity.*;
import com.luppo.farmacia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final PharmacyProductRepository pharmacyProductRepository;
    private final SrsProductRepository srsProductRepository;
    private final SrsPriceRepository srsPriceRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final MasterProductRepository masterProductRepository;

    @Transactional(readOnly = true)
    public ProductComparisonDto buildComparison(MasterProduct product) {
        // 1. Get Pharmacy Products
        List<PharmacyProduct> pharmacyProducts = pharmacyProductRepository.findByMasterProductIdWithPharmacy(product.getId());

        // 2. Get SRS Reference
        Optional<SrsProduct> srsProductOpt = srsProductRepository.findByMasterProductId(product.getId());
        SrsReferenceDto srsDto = null;
        BigDecimal pvmp = null;

        if (srsProductOpt.isPresent()) {
            SrsProduct srs = srsProductOpt.get();
            Optional<SrsPrice> priceOpt = srsPriceRepository.findTopBySrsProductIdOrderByCreatedAtDesc(srs.getId());
            if (priceOpt.isPresent()) {
                SrsPrice sp = priceOpt.get();
                pvmp = sp.getPvmp();
                srsDto = SrsReferenceDto.builder()
                        .healthRegistration(srs.getHealthRegistration())
                        .chm(srs.getChm())
                        .productName(srs.getProductName())
                        .activeIngredient(srs.getActiveIngredient())
                        .concentration(srs.getConcentration())
                        .pharmaceuticalForm(srs.getPharmaceuticalForm())
                        .presentation(srs.getPresentation())
                        .laboratory(srs.getLaboratory())
                        .pvmp(sp.getPvmp())
                        .pvmpUnit(sp.getPvmpUnit())
                        .pvmpPresentation(sp.getPvmpPresentation())
                        .marketPrice(sp.getMarketPrice())
                        .pvmpType(sp.getPvmpType())
                        .effectiveDate(sp.getEffectiveDate())
                        .sourceUrl(srs.getSourceUrl())
                        .lastVerifiedAt(srs.getLastVerifiedAt())
                        .build();
            }
        }

        // 3. Map Pharmacy Prices and calculate differences
        final BigDecimal finalPvmp = pvmp;
        List<PharmacyPriceDto> prices = pharmacyProducts.stream().map(pp -> {
            BigDecimal effectivePrice = pp.getCurrentOfferPrice() != null ? pp.getCurrentOfferPrice() : pp.getCurrentPrice();
            BigDecimal diff = null;
            BigDecimal percent = null;

            if (finalPvmp != null && effectivePrice != null) {
                diff = effectivePrice.subtract(finalPvmp).setScale(2, RoundingMode.HALF_UP);
                if (finalPvmp.compareTo(BigDecimal.ZERO) > 0) {
                    percent = diff.divide(finalPvmp, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                }
            }

            return PharmacyPriceDto.builder()
                    .pharmacyId(pp.getPharmacy().getId())
                    .pharmacyName(pp.getPharmacy().getName())
                    .pharmacyCode(pp.getPharmacy().getCode())
                    .originalName(pp.getOriginalName())
                    .presentation(pp.getPresentation())
                    .price(pp.getCurrentPrice())
                    .offerPrice(pp.getCurrentOfferPrice())
                    .isAvailable(pp.getIsAvailable())
                    .url(pp.getUrl())
                    .imageUrl(pp.getImageUrl())
                    .lastUpdated(pp.getLastScrapedAt())
                    .differenceVsPvmp(diff)
                    .percentageVsPvmp(percent)
                    .build();
        }).collect(Collectors.toList());

        // Calculate min & max price
        BigDecimal lowest = prices.stream()
                .map(p -> p.getOfferPrice() != null ? p.getOfferPrice() : p.getPrice())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        BigDecimal highest = prices.stream()
                .map(p -> p.getPrice())
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        // 4. Get Price History
        List<PriceHistoryPointDto> history = priceHistoryRepository.findByMasterProductIdOrderByCheckedAtAsc(product.getId())
                .stream().map(ph -> PriceHistoryPointDto.builder()
                        .pharmacyId(ph.getPharmacyProduct().getPharmacy().getId())
                        .pharmacyName(ph.getPharmacyProduct().getPharmacy().getName())
                        .price(ph.getPrice())
                        .offerPrice(ph.getOfferPrice())
                        .isAvailable(ph.getIsAvailable())
                        .checkedAt(ph.getCheckedAt())
                        .build())
                .collect(Collectors.toList());

        // 5. Get Equivalent Products (same active ingredient)
        List<EquivalentProductDto> equivalents = masterProductRepository.findByActiveIngredientIgnoreCase(product.getActiveIngredient())
                .stream()
                .filter(eq -> !eq.getId().equals(product.getId()))
                .map(eq -> {
                    List<PharmacyProduct> eqProds = pharmacyProductRepository.findByMasterProductId(eq.getId());
                    BigDecimal eqMin = eqProds.stream()
                            .map(p -> p.getCurrentOfferPrice() != null ? p.getCurrentOfferPrice() : p.getCurrentPrice())
                            .filter(Objects::nonNull)
                            .min(Comparator.naturalOrder())
                            .orElse(null);

                    BigDecimal eqMax = eqProds.stream()
                            .map(PharmacyProduct::getCurrentPrice)
                            .filter(Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    BigDecimal eqPvmp = srsProductRepository.findByMasterProductId(eq.getId())
                            .flatMap(sp -> srsPriceRepository.findTopBySrsProductIdOrderByCreatedAtDesc(sp.getId()))
                            .map(SrsPrice::getPvmp)
                            .orElse(null);

                    return EquivalentProductDto.builder()
                            .id(eq.getId())
                            .name(eq.getName())
                            .brand(eq.getBrand())
                            .laboratory(eq.getLaboratory())
                            .activeIngredient(eq.getActiveIngredient())
                            .concentration(eq.getConcentration())
                            .pharmaceuticalForm(eq.getPharmaceuticalForm())
                            .presentation(eq.getPresentation())
                            .minPrice(eqMin)
                            .maxPrice(eqMax)
                            .pvmpSrs(eqPvmp)
                            .build();
                })
                .collect(Collectors.toList());

        return ProductComparisonDto.builder()
                .id(product.getId())
                .name(product.getName())
                .activeIngredient(product.getActiveIngredient())
                .concentration(product.getConcentration())
                .pharmaceuticalForm(product.getPharmaceuticalForm())
                .administrationRoute(product.getAdministrationRoute())
                .brand(product.getBrand())
                .laboratory(product.getLaboratory())
                .healthRegistration(product.getHealthRegistration())
                .chm(product.getChm())
                .presentation(product.getPresentation())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .lowestPrice(lowest)
                .highestPrice(highest)
                .srsReference(srsDto)
                .pharmacyPrices(prices)
                .equivalentProducts(equivalents)
                .priceHistory(history)
                .build();
    }
}
