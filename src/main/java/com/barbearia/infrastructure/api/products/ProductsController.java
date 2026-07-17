package com.barbearia.infrastructure.api.products;

import com.barbearia.application.dto.request.ProductRequestDTO;
import com.barbearia.application.dto.response.ProductResponseDTO;
import com.barbearia.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productsService;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable){
        Page<ProductResponseDTO> products = productsService.findProducts(name, category, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID id) {
        ProductResponseDTO product = productsService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productsService.createProduct(productRequestDTO);
        return ResponseEntity.ok(createdProduct);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productsService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productsService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
