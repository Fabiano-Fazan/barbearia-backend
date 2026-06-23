package com.barbearia.application.service;


import com.barbearia.application.dto.request.ProductRequestDTO;
import com.barbearia.application.dto.response.ProductResponseDTO;
import com.barbearia.domain.entities.Products;
import com.barbearia.infrastructure.persistence.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductsRepository productsRepository;

    public ProductResponseDTO findById(UUID id) {
        return productsRepository.findById(id)
                .map(ProductResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Product not found"));
     }

    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productsRepository.findAll(pageable)
                .map(ProductResponseDTO::new);
     }

     public Page<ProductResponseDTO> findByCategory(Pageable pageable, String category) {
        return productsRepository.findByCategory(category, pageable)
                .map(ProductResponseDTO::new);
     }

     public Page<ProductResponseDTO> findByName(Pageable pageable, String name) {
        return productsRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(ProductResponseDTO::new);
     }

     @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        var product = new Products();
        processData(product, productRequestDTO);
        product.setCreatedAt(LocalDateTime.now());
        productsRepository.save(product);
        return new ProductResponseDTO(product);
     }

     @Transactional
     public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        var product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        processData(product, productRequestDTO);
        product.setUpdatedAt(LocalDateTime.now());
        productsRepository.save(product);
        return new ProductResponseDTO(product);
     }


     private void processData(Products products,ProductRequestDTO productRequestDTO) {
        products.setName(productRequestDTO.name());
        products.setCategory(productRequestDTO.category());
        products.setPrice(productRequestDTO.price());
        products.setDescription(productRequestDTO.description());
        products.setProductType(productRequestDTO.type());
        products.setActive(productRequestDTO.isActive());
     }
}
