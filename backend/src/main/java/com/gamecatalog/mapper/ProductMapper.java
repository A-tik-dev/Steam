package com.gamecatalog.mapper;

import com.gamecatalog.dto.ProductDTO;
import com.gamecatalog.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        dto.setCreationDate(product.getCreationDate());
        dto.setIsDeleted(product.getIsDeleted());
        dto.setCreatorUserId(product.getCreatorUserId());
        return dto;
    }
}
