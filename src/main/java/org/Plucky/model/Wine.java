package org.Plucky.model;

public class Wine {
    private String winery;
    private String wine;
    private String rating;
    private String reviews;
    private String location;
    private String image;

    public Wine(String winery, String wine, String rating, String reviews, String location, String image) {
        this.winery = winery;
        this.wine = wine;
        this.rating = rating;
        this.reviews = reviews;
        this.location = location;
        this.image = image;
    }

    // Getters and setters
    public String getWinery() { return winery; }
    public void setWinery(String winery) { this.winery = winery; }
    
    public String getWine() { return wine; }
    public void setWine(String wine) { this.wine = wine; }
    
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    
    public String getReviews() { return reviews; }
    public void setReviews(String reviews) { this.reviews = reviews; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @Override
    public String toString() {
        return "Wine{" +
                "winery='" + winery + '\'' +
                ", wine='" + wine + '\'' +
                ", rating=" + rating +
                ", reviews=" + reviews +
                ", location='" + location + '\'' +
                '}';
    }
} 