package com.gamecatalog.dto;

import java.util.List;

// EN: Details payload that groups one product with all visible comments.
// RU: Payload деталей, который объединяет один продукт со всеми видимыми комментариями.
public class ProductWithCommentsDTO {
    private ProductDTO product;
    private List<CommentDTO> comments;

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
