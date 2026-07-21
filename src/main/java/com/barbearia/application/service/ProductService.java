package com.barbearia.application.service;


import com.barbearia.application.dto.request.ProductRequestDTO;
import com.barbearia.application.dto.response.ProductResponseDTO;
import com.barbearia.domain.entities.Products;
import com.barbearia.infrastructure.persistence.ProductsRepository;
import com.barbearia.infrastructure.persistence.specifications.ProductsSpecifications;
import com.barbearia.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductsRepository productsRepository;

    public ProductResponseDTO findById(UUID id) {
        return productsRepository.findById(id)
                .map(ProductResponseDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
     }

    public Page<ProductResponseDTO> findProducts(String name, String category, Pageable pageable) {
        Specification<Products> specification = Specification
                .where(ProductsSpecifications.hasName(name))
                .and(ProductsSpecifications.hasCategory(category));
        return productsRepository.findAll(specification, pageable)
                .map(ProductResponseDTO::new);
     }

     @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Products product = new Products();
        processData(product, productRequestDTO);
        productsRepository.save(product);
        return new ProductResponseDTO(product);
     }

     @Transactional
     public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        processData(product, productRequestDTO);
        productsRepository.save(product);
        return new ProductResponseDTO(product);
     }

     @Transactional
     public void deleteProduct(UUID id) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setIsActive(false);
        productsRepository.save(product);
     }

     private void processData(Products products,ProductRequestDTO productRequestDTO) {
        products.setName(productRequestDTO.name());
        products.setCategory(productRequestDTO.category());
        products.setPrice(productRequestDTO.price());
        products.setDescription(productRequestDTO.description());
        products.setProductType(productRequestDTO.type());
        products.setDurationInMinutes(productRequestDTO.durationInMinutes());
        products.setIsActive(productRequestDTO.isActive());
     }
}
