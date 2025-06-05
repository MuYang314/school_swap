package com.example.school_swap;

import android.annotation.SuppressLint;

import java.util.List;

public class Product {
    private int id;
    private String title;
    private double price;
    private String description;
    private List<String> imageUrls;
    private String sellerName;
    private String sellerMeta;
    private boolean isFavorite;      // 是否收藏

    // 无参构造方法
    public Product() {}

    // 全参构造方法
    public Product(int id, String title, double price, String description,
                   List<String> imageUrls, String sellerName, String sellerMeta,
                   boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrls = imageUrls;
        this.sellerName = sellerName;
        this.sellerMeta = sellerMeta;
        this.isFavorite = isFavorite;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getSellerName() { return sellerName; }
    public String getSellerMeta() { return sellerMeta; }
    public boolean isFavorite() { return isFavorite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setSellerMeta(String sellerMeta) { this.sellerMeta = sellerMeta; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    // 价格格式化方法
    @SuppressLint("DefaultLocale")
    public String getFormattedPrice() {
        return "¥" + String.format("%.2f", price);
    }
}
