package com.barbearia.catalog.application.service;


import com.barbearia.catalog.application.dto.ProductRequestDTO;
import com.barbearia.catalog.application.dto.ProductResponseDTO;
import com.barbearia.catalog.application.dto.ProductMapper;
import com.barbearia.catalog.domain.model.Product;
import com.barbearia.catalog.infrastructure.persistence.ProductRepository;
import com.barbearia.catalog.infrastructure.persistence.ProductSpecifications;
import com.barbearia.shared.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogApplicationService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findProducts(String name, String category, UUID productId, Pageable pageable) {
        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasName(name))
                .and(ProductSpecifications.hasCategory(category))
                .and(ProductSpecifications.hasId(productId));
        return productRepository.findAll(specification, pageable)
                .map(mapper::toResponse);
     }

     @Transactional(readOnly = true)
     public List<Product> getAllProductsById(List<UUID> id) {
        return productRepository.findAllById(id);
     }

     @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        processData(product, productRequestDTO);
        return mapper.toResponse(productRepository.save(product));
     }

     @Transactional
     public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        processData(product, productRequestDTO);
        return mapper.toResponse(productRepository.save(product));
     }

     @Transactional
     public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
     }

     private void processData(Product product, ProductRequestDTO productRequestDTO) {
        product.setName(productRequestDTO.name());
        product.setCategory(productRequestDTO.category());
        product.setPrice(productRequestDTO.price());
        product.setDescription(productRequestDTO.description());
        product.setProductType(productRequestDTO.type());
        product.setDurationInMinutes(productRequestDTO.durationInMinutes());
     }
}
