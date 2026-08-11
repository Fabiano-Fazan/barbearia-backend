package com.barbearia.product;

import com.barbearia.product.dto.ProductRequestDTO;
import com.barbearia.product.dto.ProductResponseDTO;
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
public class ProductController {

    private final ProductService productsService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BARBER', 'CLIENT') ")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID productId,
            Pageable pageable){
        Page<ProductResponseDTO> products = productsService.findProducts(name, category, productId, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
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
