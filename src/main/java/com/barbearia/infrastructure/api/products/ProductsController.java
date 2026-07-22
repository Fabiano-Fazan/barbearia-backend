package com.barbearia.infrastructure.api.products;

import com.barbearia.application.dto.request.ProductRequestDTO;
import com.barbearia.application.dto.response.ProductResponseDTO;
import com.barbearia.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productsService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT') ")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable){
        Page<ProductResponseDTO> products = productsService.findProducts(name, category, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT') ")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID id) {
        ProductResponseDTO product = productsService.findById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or  hasRole('BARBER') ")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productsService.createProduct(productRequestDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or  hasRole('BARBER') ")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productsService.updateProduct(id, productRequestDTO);
        return new  ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or  hasRole('BARBER') ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productsService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
