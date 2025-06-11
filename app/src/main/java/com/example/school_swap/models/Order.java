package com.example.school_swap.models;

public class Order {
    private String orderId;
    private String image;
    private String orderStatus;
    private String orderTitle;
    private Double price;

    public Order(String orderId, String orderStatus, String orderTitle, Double price) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderTitle = orderTitle;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getImage() {
        return image;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public Double getPrice() {
        return price;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public void setImage(String image) {
        this.image = image;
    }
}