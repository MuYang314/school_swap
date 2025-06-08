package com.example.school_swap.network;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.school_swap.Product;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductHttpClient extends BaseHttpClient {
    public static void fetchProducts(int page, int pageSize, ApiCallback<PaginatedResponse<ProductData>> callback) {
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
                    String responseData = response.body().string();
                    try {
                        Type type = new TypeToken<BaseResponse<PaginatedResponse<ProductData>>>() {}.getType();
                        BaseResponse<PaginatedResponse<ProductData>> productResponse = gson.fromJson(responseData, type);
                        callback.onSuccess(productResponse);
                    } catch (Exception e) {
                        callback.onError("解析响应失败: " + e.getMessage());
                        Log.d("Home解析响应失败", e.getMessage().toString());
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

    public static void productDetail(int productId, ProductDetailCallback<ProductData> callback) {
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
                            Type type = new TypeToken<BaseResponse<ProductData>>() {}.getType();
                            BaseResponse<ProductData> productDetailResponse = gson.fromJson(responseData, type);
                            if (productDetailResponse.code == 200) {
                                callback.onSuccess(productDetailResponse.data);
                            } else {
                                callback.onError(productDetailResponse.message);
                            }
                        } catch (Exception e) {
                            callback.onError("解析响应失败: " + e.getMessage());
                            Log.d("productDetail解析响应失败", Objects.requireNonNull(e.getMessage()));
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