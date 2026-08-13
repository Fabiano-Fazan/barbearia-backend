package com.barbearia.catalog.application.dto;
import com.barbearia.catalog.domain.model.Product;
import org.springframework.stereotype.Component;
@Component public class ProductMapper {
    public ProductResponseDTO toResponse(Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCategory(),
                p.getProductType().name(),
                p.getDurationInMinutes(),
                p.getPrice()); }
}
