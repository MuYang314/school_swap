package com.example.school_swap.network;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

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

    public static void publishTask() {
    }
}