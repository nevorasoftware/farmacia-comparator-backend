package com.luppo.farmacia.controller;

import com.luppo.farmacia.dto.ProductComparisonDto;
import com.luppo.farmacia.dto.ProductSearchDto;
import com.luppo.farmacia.entity.MasterProduct;
import com.luppo.farmacia.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para búsqueda, catálogo y comparativa de medicamentos y PVMP")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Listar catálogo de medicamentos", description = "Retorna todos los medicamentos registrados con resumen de precios")
    public ResponseEntity<List<ProductSearchDto>> getAllProducts() {
        return ResponseEntity.ok(productService.searchProducts(null));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscador de medicamentos", description = "Búsqueda por nombre, principio activo, marca o registro sanitario")
    public ResponseEntity<List<ProductSearchDto>> searchProducts(@RequestParam(value = "q", required = false) String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Sugerencias de autocompletado", description = "Retorna nombres de productos que coinciden con el término")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam(value = "q", required = false) String query) {
        return ResponseEntity.ok(productService.getSuggestions(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener medicamento por ID")
    public ResponseEntity<MasterProduct> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/{id}/comparison")
    @Operation(summary = "Comparativa detallada del medicamento", description = "Retorna precios por farmacia, PVMP oficial de la SRS, sustitutos e histórico")
    public ResponseEntity<ProductComparisonDto> getProductComparison(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductComparison(id));
    }
}
