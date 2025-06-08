package com.example.school_swap.network;

import android.content.Context;
import android.util.Log;

import com.example.school_swap.models.Task;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

public class TaskHttpClient extends BaseHttpClient {
    public static void fetchTask(int page, int pageSize, ApiCallback<PaginatedResponse<TaskData>> callback) {
        try {
            String url = BASE_URL + "/api/tasks/list?page=" + page + "&page_size=" + pageSize;
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
                        Type type = new TypeToken<BaseResponse<PaginatedResponse<TaskData>>>() {}.getType();
                        BaseResponse<PaginatedResponse<TaskData>> taskResponse = gson.fromJson(responseData, type);
                        if (taskResponse.code == 200) {
                            callback.onSuccess(taskResponse.data);
                        } else {
                            callback.onError(taskResponse.message);
                        }
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

    public static void taskDetail(int taskId, ApiCallback<TaskData> callback) {
        try {
            String url = BASE_URL + "/api/tasks/" + taskId;
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
                            Type type = new TypeToken<BaseResponse<TaskData>>() {}.getType();
                            BaseResponse<TaskData> taskDetailResponse = gson.fromJson(responseData, type);
                            if (taskDetailResponse.code == 200) {
                                callback.onSuccess(taskDetailResponse.data);
                            } else {
                                callback.onError(taskDetailResponse.message);
                            }
                        } catch (Exception e) {
                            callback.onError("解析响应失败: " + e.getMessage());
                            Log.d("taskDetail解析响应失败", Objects.requireNonNull(e.getMessage()));
                        }
                    } else {
                        callback.onError("请求失败，状态码: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("请求构建失败: " + e.getMessage());
            Log.d("请求构建失败", Objects.requireNonNull(e.getMessage()));

        }
    }

    public static void publishTask(Context context, Task task, ApiCallback<String> callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("publisher", task.getPublisherId());
            json.put("title", task.getTitle());
            json.put("reward", task.getPrice());
            json.put("deadline", task.getDeadline());
            json.put("description", task.getDescription());
            json.put("category", task.getCategory());

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/tasks/publish")
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
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }

    }
}