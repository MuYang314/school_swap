package com.example.school_swap.network;

import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TaskHttpClient extends BaseHttpClient {
    public static void fetchTasks(LoginCallback<List<TaskData>> callback) {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/tasks/list")
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
                        Type type = new TypeToken<BaseResponse<List<TaskData>>>() {}.getType();
                        BaseResponse<List<TaskData>> taskResponse = gson.fromJson(responseData, type);

                        if (taskResponse.code == 200) {
                            callback.onSuccess(taskResponse.data);
                        } else {
                            callback.onError(taskResponse.message);
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

    public static void publishTask(TaskData taskData, LoginCallback<TaskData> callback) {
        // 实现逻辑参考login方法，使用TaskData作为请求体和响应体
    }
}