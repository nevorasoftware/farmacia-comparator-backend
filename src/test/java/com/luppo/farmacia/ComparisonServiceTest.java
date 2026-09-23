package com.luppo.farmacia;

import com.luppo.farmacia.dto.ProductComparisonDto;
import com.luppo.farmacia.entity.*;
import com.luppo.farmacia.repository.*;
import com.luppo.farmacia.service.ComparisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComparisonServiceTest {

    @Mock
    private PharmacyProductRepository pharmacyProductRepository;
    @Mock
    private SrsProductRepository srsProductRepository;
    @Mock
    private SrsPriceRepository srsPriceRepository;
    @Mock
    private PriceHistoryRepository priceHistoryRepository;
    @Mock
    private MasterProductRepository masterProductRepository;

    @InjectMocks
    private ComparisonService comparisonService;

    private MasterProduct product;
    private Pharmacy pharmacy;
    private PharmacyProduct pharmacyProduct;

    @BeforeEach
    void setUp() {
        product = MasterProduct.builder()
                .id(1L)
                .name("Acetaminofén 500 mg")
                .activeIngredient("Acetaminofén")
                .concentration("500 mg")
                .brand("MK")
                .build();

        pharmacy = Pharmacy.builder()
                .id(1L)
                .name("Farmacias CEFAFA")
                .code("CEFAFA")
                .baseUrl("https://cefafa.com")
                .build();

        pharmacyProduct = PharmacyProduct.builder()
                .id(1L)
                .pharmacy(pharmacy)
                .masterProduct(product)
                .originalName("Acetaminofén 500 mg x 20")
                .currentPrice(BigDecimal.valueOf(5.50))
                .currentOfferPrice(BigDecimal.valueOf(5.20))
                .url("https://cefafa.com/prod/1")
                .isAvailable(true)
                .build();
    }

    @Test
    void shouldBuildComparisonWithPvmpDifference() {
        SrsProduct srsProduct = SrsProduct.builder().id(10L).masterProduct(product).healthRegistration("F123").build();
        SrsPrice srsPrice = SrsPrice.builder().srsProduct(srsProduct).pvmp(BigDecimal.valueOf(6.00)).build();

        when(pharmacyProductRepository.findByMasterProductIdWithPharmacy(1L)).thenReturn(List.of(pharmacyProduct));
        when(srsProductRepository.findByMasterProductId(1L)).thenReturn(Optional.of(srsProduct));
        when(srsPriceRepository.findTopBySrsProductIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.of(srsPrice));
        when(priceHistoryRepository.findByMasterProductIdOrderByCheckedAtAsc(1L)).thenReturn(Collections.emptyList());
        when(masterProductRepository.findByActiveIngredientIgnoreCase("Acetaminofén")).thenReturn(List.of(product));

        ProductComparisonDto result = comparisonService.buildComparison(product);

        assertNotNull(result);
        assertEquals("Acetaminofén 500 mg", result.getName());
        assertEquals(1, result.getPharmacyPrices().size());
        assertEquals(BigDecimal.valueOf(5.20), result.getLowestPrice());
        assertNotNull(result.getSrsReference());
        assertEquals(BigDecimal.valueOf(6.00), result.getSrsReference().getPvmp());

        // Effective price is 5.20 (offer). Difference vs 6.00 PVMP is -0.80
        assertEquals(0, BigDecimal.valueOf(-0.80).compareTo(result.getPharmacyPrices().get(0).getDifferenceVsPvmp()));
    }
}
