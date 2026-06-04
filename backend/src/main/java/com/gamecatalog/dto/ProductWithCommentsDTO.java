package com.gamecatalog.dto;

import java.util.List;

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
