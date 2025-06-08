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


    public static class PaginatedResponse<T> {
        public List<T> goods;  // 商品列表
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
        public ProductOwner owner;

        public static class ProductOwner {
            public int id;
            public String nickname;
            public String avatar_url;
            public int credit_score;
        }
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

    public interface ApiCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    public interface TaskCallback {
        void onSuccess(List<Task> tasks);
        void onError(String error);
    }

    public interface ResponseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface LoginCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    public interface ProductCallback<T> {
        void onSuccess(T products);
        void onError(String error);
    }

    public interface ImageUploadCallback {
        void onAllImagesUploaded(List<String> imageUids);
        void onError(String error);
    }

    public interface ProductDetailCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    @SuppressLint("Range")
    protected static String getFileName(ContentResolver contentResolver, Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}