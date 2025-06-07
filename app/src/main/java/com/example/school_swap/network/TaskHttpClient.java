package com.example.school_swap.network;

import com.example.school_swap.Task;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class TaskHttpClient extends BaseHttpClient {
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