package com.example.school_swap;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task implements Serializable {
    private int id;
    private String title;
    private String category;
    private String description;
    private double price;           // 使用double更精确
    private String deadline;        // 字符串格式日期
    private Date deadlineDate;      // 解析后的日期对象
    private String publisherId;     // 发布者ID
    private String publisherName;   // 发布者姓名
    private String publisherAvatar; // 发布者头像
    private int status;             // 任务状态：0-待接单 1-已接单 2-已完成 3-已取消
    private boolean isUrgent;       // 是否加急任务
    private String location;        // 任务地点
    private Date createTime;        // 创建时间
    private Date acceptTime;        // 接单时间
    private Date finishTime;        // 完成时间

    // 无参构造方法
    public Task() {}

    // 主要信息构造方法
    public Task(int id, String title, String category, String description,
                double price, String deadline) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.deadline = deadline;
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
    public String getPublisherId() { return publisherId; }
    public String getPublisherName() { return publisherName; }
    public String getPublisherAvatar() { return publisherAvatar; }
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
    public void setPublisherId(String publisherId) { this.publisherId = publisherId; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    public void setPublisherAvatar(String publisherAvatar) { this.publisherAvatar = publisherAvatar; }
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
                // 支持两种常见日期格式
                SimpleDateFormat sdf;
                if (deadline.contains(":")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                } else {
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                }
                deadlineDate = sdf.parse(deadline);
            } catch (ParseException e) {
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
