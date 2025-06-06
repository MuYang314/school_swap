package com.example.school_swap.network;

import com.example.school_swap.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class HttpClient {
    private static final String BASE_URL = "http://192.168.198.76:8000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

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
                                    product.setImageUrls(imageUrls);
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
}