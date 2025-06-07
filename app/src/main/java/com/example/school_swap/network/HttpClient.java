package com.example.school_swap.network;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.school_swap.Product;
import com.example.school_swap.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpClient {
    private static final String BASE_URL = "http://192.168.198.76:8000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static class LoginResponse {
        public int code;
        public String message;
        public UserData data;

        public static class UserData {
            public int id;
            public String nickname;
            public String email;
            public String avatar_url;
            public int credit_score;
            public String token;
        }
    }

    public static class ProductDetailResponse {
        public int code;
        public String message;
        public Data data;
        public static class Data {
            public int id;
            public String title;
            public String description;
            public String category;
            public Double price;
            public List<String> images;
            public String status;
            public String created_at;
            public User owner;

        }
        public static class User {
            public int id;
            public String nickname;
            public String avatar_url;
            public int credit_score;
        }
    }

    public static class TaskDetailResponse {
        public int code;
        public String message;
        public Data data;
        public static class Data {

        }
    }

    public interface TaskCallback {
        void onSuccess(List<Task> tasks);
        void onError(String error);
    }

    public interface ResponseCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse.UserData userData);
        void onError(String error);
    }

    public interface ProductCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }

    public interface ImageUploadCallback {
        void onAllImagesUploaded(List<String> imageUids);
        void onError(String error);
    }

    public interface ProductDetailCallback {
        void onSuccess(ProductDetailResponse.Data response);
        void onError(String error);
    }

    public static void login(String email, String password, LoginCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("登录失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    try {
                        LoginResponse loginResponse = gson.fromJson(responseData, LoginResponse.class);
                        if (loginResponse.code == 200) {
                            callback.onSuccess(loginResponse.data);
                        } else {
                            callback.onError(loginResponse.message);
                        }
                    } catch (Exception e) {
                        callback.onError("解析响应失败: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }

    public static void register(String email, String password, String confirmPassword, String verificationCode, ResponseCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("confirm_password", confirmPassword);
            json.put("verification_code", verificationCode);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/register")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("注册失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int code = jsonResponse.getInt("code");
                        String message = jsonResponse.getString("message");

                        if (code == 200) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError("解析响应失败: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }

    public static void resetPassword(String email, String password, String confirmPassword, String verificationCode, ResponseCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("confirm_password", confirmPassword);
            json.put("verification_code", verificationCode);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/passwd_reset")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("密码重置失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int code = jsonResponse.getInt("code");
                        String message = jsonResponse.getString("message");

                        if (code == 200) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError("解析响应失败: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }

    public static void getVerificationCode(String email, ResponseCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/verify_code")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("获取验证码失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int code = jsonResponse.getInt("code");
                        String message = jsonResponse.getString("message");

                        if (code == 200) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError("解析响应失败: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }

    public static void fetchProducts(int page, int pageSize, ProductCallback callback) {
        try {
            String url = BASE_URL + "/api/goods/list?page=" + page + "&page_size=" + pageSize;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            int code = jsonResponse.getInt("code");
                            if (code == 200) {
                                JSONObject data = jsonResponse.getJSONObject("data");
                                List<Product> products = new ArrayList<>();
                                for (int i = 0; i < data.getJSONArray("goods").length(); i++) {
                                    JSONObject good = data.getJSONArray("goods").getJSONObject(i);
                                    int id = good.getInt("id");
                                    String title = good.getString("title");
                                    double price = good.getDouble("price");
                                    String category = good.getString("category");
                                    String created_at = good.getString("created_at");
                                    int count = good.getInt("count");
                                    List<String> imageUrls = gson.fromJson(good.getJSONArray("images").toString(), new TypeToken<List<String>>() {}.getType());
                                    Product product = new Product();
                                    product.setId(id);
                                    product.setTitle(title);
                                    product.setPrice(price);
                                    product.setCategory(category);
                                    product.setImageUids(imageUrls);
                                    product.setCreated_at(created_at);
                                    product.setCount(count);
                                    products.add(product);
                                }
                                callback.onSuccess(products);
                            } else {
                                callback.onError(jsonResponse.getString("message"));
                            }
                        } catch (Exception e) {
                            callback.onError("解析响应失败: " + e.getMessage());
                        }
                    } else {
                        callback.onError("请求失败，状态码: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }

    public static void uploadImages(Context context, List<Uri> imageUris, ImageUploadCallback callback) {
        List<String> imageUids = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        for (Uri uri : imageUris) {
            executorService.execute(() -> {
                try {
                    ContentResolver contentResolver = context.getContentResolver();

                    // 获取文件名
                    String fileName = getFileName(contentResolver, uri);

                    // 通过 URI 读取图片的二进制数据
                    InputStream inputStream = contentResolver.openInputStream(uri);
                    byte[] imageBytes = new byte[inputStream.available()];
                    inputStream.read(imageBytes);
                    inputStream.close();

                    // 创建 RequestBody
                    RequestBody fileBody = RequestBody.create(imageBytes, MediaType.get("image/*"));
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", fileName, fileBody)
                            .build();

                    // 构造请求
                    Request request = new Request.Builder()
                            .url(BASE_URL + "/api/upload")
                            .post(requestBody)
                            .build();

                    // 执行请求
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        int code = jsonResponse.getInt("code");
                        if (code == 200) {
                            String uid = jsonResponse.getString("uid");
                            synchronized (imageUids) {
                                imageUids.add(uid);
                            }
                        }
                    }
                } catch (IOException | org.json.JSONException e) {
                    callback.onError("图片上传失败: " + e.getMessage());
                } finally {
                    if (counter.incrementAndGet() == imageUris.size()) {
                        callback.onAllImagesUploaded(imageUids);
                    }
                }
            });
        }
    }

    // 获取文件名的辅助方法
    @SuppressLint("Range")
    private static String getFileName(ContentResolver contentResolver, Uri uri) {
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

    /**
     * 发布商品（自动处理图片上传）
     * @param context  上下文
     * @param product  商品对象（需包含除图片UID外的其他字段）
     * @param imageUris 图片Uri列表
     * @param callback 统一回调接口
     */
    public static void publishProduct(Context context, Product product, List<Uri> imageUris, ResponseCallback callback) {
        // 第一步：上传所有图片
        uploadImages(context, imageUris, new ImageUploadCallback() {
            @Override
            public void onAllImagesUploaded(List<String> imageUids) {
                // 图片上传成功，将UIDs设置到商品对象
                product.setImageUids(imageUids);

                // 第二步：构造JSON并发布商品
                try {
                    JSONObject json = new JSONObject();
                    json.put("owner_id", product.getOwner_id());
                    json.put("title", product.getTitle());
                    json.put("price", product.getPrice());
                    json.put("count", product.getCount());
                    json.put("description", product.getDescription());
                    json.put("category", product.getCategory());
                    json.put("images", new JSONArray(imageUids)); // 使用上传后的UID列表

                    RequestBody body = RequestBody.create(json.toString(), JSON);
                    Request request = new Request.Builder()
                            .url(BASE_URL + "/api/goods/publish")
                            .post(body)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            callback.onError("发布失败: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            try {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                int code = jsonResponse.getInt("code");
                                String message = jsonResponse.getString("message");

                                if (code == 200) {
                                    callback.onSuccess(message);
                                } else {
                                    callback.onError(message);
                                }
                            } catch (Exception e) {
                                callback.onError("解析响应失败: " + e.getMessage());
                            }
                        }
                    });
                } catch (JSONException e) {
                    callback.onError("JSON构造失败: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                // 图片上传失败时直接回调错误
                callback.onError(errorMessage);
            }
        });
    }

    public static void productDetail(int productId, ProductDetailCallback callback) {
        try {
            String url = BASE_URL + "/api/goods/" + productId;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            ProductDetailResponse productDetailResponse = gson.fromJson(responseData, ProductDetailResponse.class);
                            if (productDetailResponse.code == 200) {
                                callback.onSuccess(productDetailResponse.data);
                            } else {
                                callback.onError(productDetailResponse.message);
                            }
                        } catch (Exception e) {
                            callback.onError("解析响应失败: " + e.getMessage());
                        }
                    } else {
                        callback.onError("请求失败，状态码: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }

    public static void fetchTasks(TaskCallback callback) {
        try {
            String url = BASE_URL + "/api/tasks/list";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("请求失败: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            int code = jsonResponse.getInt("code");
                            if (code == 200) {
                                JSONObject data = jsonResponse.getJSONObject("data");
                                List<Task> tasks = new ArrayList<>();
                                for (int i = 0; i < data.getJSONArray("tasks").length(); i++) {
                                    JSONObject taskJson = data.getJSONArray("tasks").getJSONObject(i);
                                    int id = taskJson.getInt("id");
                                    String title = taskJson.getString("title");
                                    String category = taskJson.getString("category");
                                    double price = taskJson.getDouble("reward");
                                    String description = taskJson.getString("description");
                                    String meta = taskJson.getString("meta");
                                    String deadline = taskJson.getString("deadline");
                                    Task task = new Task();
                                    task.setId(id);
                                    task.setTitle(title);
                                    task.setCategory(category);
                                    task.setPrice(price);
                                    task.setDescription(description);
                                    task.setDeadline(deadline);
                                    tasks.add(task);
                                }
                                callback.onSuccess(tasks);
                            } else {
                                callback.onError(jsonResponse.getString("message"));
                            }
                        } catch (Exception e) {
                            callback.onError("解析响应失败: " + e.getMessage());
                        }
                    } else {
                        callback.onError("请求失败，状态码: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
        }
    }


    public static void publishTask() {}

}