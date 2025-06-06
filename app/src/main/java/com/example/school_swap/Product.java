package com.example.school_swap;

import android.annotation.SuppressLint;

import java.util.List;

public class Product {
    private int id;
    private int owner_id;
    private String title;
    private double price;
    private int count;
    private String category;
    private String description;
    private List<String> imageUids;
    private String created_at;
    private String sellerName;
    private String sellerMeta;
    private boolean isFavorite;      // 是否收藏

    // 无参构造方法
    public Product() {}

    // 全参构造方法
    public Product(int id, String title, double price, int count, String description,
                   List<String> imageUids, String sellerName, String sellerMeta,
                   boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.count = count;
        this.description = description;
        this.imageUids = imageUids;
        this.sellerName = sellerName;
        this.sellerMeta = sellerMeta;
        this.isFavorite = isFavorite;
    }

    // Getters
    public int getId() { return id; }

    public int getOwner_id() {
        return owner_id;
    }

    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getCount() { return count; }
    public String getCategory() {return category; }
    public String getDescription() { return description; }
    public List<String> getImageUids() { return imageUids; }
    public String getCreated_at() { return created_at; }
    public String getSellerName() { return sellerName; }
    public String getSellerMeta() { return sellerMeta; }
    public boolean isFavorite() { return isFavorite; }

    // Setters
    public void setId(int id) { this.id = id; }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
    public void setCount(int count) { this.count = count; }
    public void setCategory(String category) {this.category = category;}
    public void setDescription(String description) { this.description = description; }
    public void setImageUids(List<String> imageUids) { this.imageUids = imageUids; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setSellerMeta(String sellerMeta) { this.sellerMeta = sellerMeta; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    // 价格格式化方法
    @SuppressLint("DefaultLocale")
    public String getFormattedPrice() {
        return "¥" + String.format("%.2f", price);
    }
}
