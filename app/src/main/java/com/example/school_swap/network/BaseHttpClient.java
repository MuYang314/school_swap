package com.example.school_swap.network;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.school_swap.Product;
import com.example.school_swap.Task;
import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseHttpClient {
    protected static final String BASE_URL = "http://192.168.198.76:8000";
    protected static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    protected static final OkHttpClient client = new OkHttpClient();
    protected static final Gson gson = new Gson();
    protected static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    // 分页类
    public static class PaginatedResponse<T> {
        public List<T> list;
        public int total;      // 总条目数
        public int pages;      // 总页数
        public int current_page; // 当前页
    }

    // 通用响应基类
    public static class BaseResponse<T> {
        public int code;
        public String message;
        public T data;
    }

    // 用户数据结构
    public static class UserData {
        public int id;
        public String nickname;
        public String email;
        public String avatar_url;
        public int credit_score;
        public String token;
    }

    // 商品数据结构
    public static class ProductData {
        public int id;
        public String title;
        public String description;
        public String category;
        public Double price;
        public int count;
        public List<String> images;
        public String status;
        public String created_at;
        public UserData owner;
    }

    // 任务数据结构
    public static class TaskData {
        public int id;
        public String title;
        public double reward;
        public String description;
        public String category;
        public String deadline;
        public UserData publisher;
    }

    // 图片结构
    public static class Image {
        public String uid;
    }

    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}