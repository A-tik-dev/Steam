package com.gamecatalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// Maps the IGDB game payload used by the app.
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameDTO {
    private Long id;
    private String name;
    private String summary;

    @JsonProperty("cover")
    private Cover cover;

    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("release_dates")
    private List<ReleaseDate> releaseDates;

    @JsonProperty("genres")
    private List<Genre> genres;

    /**
     * Nested DTO for IGDB cover object.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cover {
        @JsonProperty("image_id")
        private String imageId;

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        // Builds a public IGDB cover URL from the image id.
        public String getUrl() {
            return imageId != null ? "https://images.igdb.com/igdb/image/upload/t_cover_big/" + imageId + ".jpg" : null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseDate {
        private Long date;

        public Long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Genre {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<ReleaseDate> getReleaseDates() {
        return releaseDates;
    }

    public void setReleaseDates(List<ReleaseDate> releaseDates) {
        this.releaseDates = releaseDates;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
