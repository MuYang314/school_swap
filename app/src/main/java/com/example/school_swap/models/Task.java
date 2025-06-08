package com.example.school_swap.models;

import android.os.Build;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Task implements Serializable {
    private int id;
    private String title;
    private String category;
    private String description;
    private double price;           // 使用double更精确
    private String deadline;        // 字符串格式日期
    private Date deadlineDate;      // 解析后的日期对象
    private int publisherId;     // 发布者ID
    private String publisherName;   // 发布者姓名
    private String publisherAvatar; // 发布者头像
    private String publisherMeta;
    private int status;             // 任务状态：0-待接单 1-已接单 2-已完成 3-已取消
    private boolean isUrgent;       // 是否加急任务
    private String location;        // 任务地点
    private Date createTime;        // 创建时间
    private Date acceptTime;        // 接单时间
    private Date finishTime;        // 完成时间

    // 无参构造方法
    public Task() {}

    // 主要信息构造方法
    public Task(int publisherId, String title, String category, String description,
                double price, String deadline) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.deadline = deadline;
        this.publisherId = publisherId;
        parseDeadline(); // 解析日期
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getDeadline() { return deadline; }
    public Date getDeadlineDate() { return deadlineDate; }
    public int getPublisherId() { return publisherId; }
    public String getPublisherName() { return publisherName; }
    public String getPublisherAvatar() { return publisherAvatar; }
    public String getPublisherMeta() { return publisherMeta; }
    public int getStatus() { return status; }
    public boolean isUrgent() { return isUrgent; }
    public String getLocation() { return location; }
    public Date getCreateTime() { return createTime; }
    public Date getAcceptTime() { return acceptTime; }
    public Date getFinishTime() { return finishTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
        parseDeadline();
    }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    public void setPublisherAvatar(String publisherAvatar) { this.publisherAvatar = publisherAvatar; }
    public void setPublisherMeta(String publisherMeta) { this.publisherMeta = publisherMeta; }
    public void setStatus(int status) { this.status = status; }
    public void setUrgent(boolean urgent) { isUrgent = urgent; }
    public void setLocation(String location) { this.location = location; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public void setAcceptTime(Date acceptTime) { this.acceptTime = acceptTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }

    // 解析截止日期字符串为Date对象
    private void parseDeadline() {
        if (deadline != null && !deadline.isEmpty()) {
            try {
                // 使用 ZonedDateTime 解析 ISO 格式的时间字符串
                ZonedDateTime zonedDateTime = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    zonedDateTime = ZonedDateTime.parse(deadline, DateTimeFormatter.ISO_DATE_TIME);
                }
                // 转换为 Date 对象
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    deadlineDate = Date.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 获取格式化的价格字符串
    public String getFormattedPrice() {
        return "¥" + String.format("%.2f", price);
    }

    // 检查任务是否已过期
    public boolean isExpired() {
        if (deadlineDate == null) return false;
        return new Date().after(deadlineDate);
    }

    // 获取任务状态文本
    public String getStatusText() {
        switch (status) {
            case 0: return "待接单";
            case 1: return "已接单";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知状态";
        }
    }

    // 获取剩余时间描述
    public String getRemainingTime() {
        if (deadlineDate == null) return "";

        long diff = deadlineDate.getTime() - System.currentTimeMillis();
        if (diff <= 0) return "已截止";

        long days = diff / (24 * 60 * 60 * 1000);
        long hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);

        if (days > 0) return days + "天" + hours + "小时后截止";
        if (hours > 0) return hours + "小时" + minutes + "分钟后截止";
        return minutes + "分钟后截止";
    }
}