package com.gamecatalog.dto;

public class FavoriteStatusDTO {
    private Long productId;
    private boolean favorite;

    public FavoriteStatusDTO(Long productId, boolean favorite) {
        this.productId = productId;
        this.favorite = favorite;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
