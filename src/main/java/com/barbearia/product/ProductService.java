package com.barbearia.product;


import com.barbearia.product.dto.ProductRequestDTO;
import com.barbearia.product.dto.ProductResponseDTO;
import com.barbearia.core.exceptions.ResourceNotFoundException;
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
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findProducts(String name, String category, UUID productId, Pageable pageable) {
        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasName(name))
                .or(ProductSpecifications.hasCategory(category))
                .or(ProductSpecifications.hasId(productId));
        return productRepository.findAll(specification, pageable)
                .map(ProductResponseDTO::new);
     }

     @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        processData(product, productRequestDTO);
        return new ProductResponseDTO( productRepository.save(product));
     }

     @Transactional
     public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        processData(product, productRequestDTO);
        return new ProductResponseDTO( productRepository.save(product));
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
