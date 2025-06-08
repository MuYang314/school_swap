package com.example.school_swap.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.school_swap.R;
import com.example.school_swap.models.Task;
import com.example.school_swap.network.BaseHttpClient;
import com.example.school_swap.network.TaskHttpClient;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvCategory, tvTitle, tvDescription, tvPrice, tvDeadline,
            tvPublisherName, tvPublisherMeta;
    private View btnBack, btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        tvCategory = findViewById(R.id.tv_task_category);
        tvTitle = findViewById(R.id.tv_task_title);
        tvDescription = findViewById(R.id.tv_task_description);
        tvPrice = findViewById(R.id.tv_task_price);
        tvDeadline = findViewById(R.id.tv_task_deadline);
        tvPublisherName = findViewById(R.id.publisher_name);
        tvPublisherMeta = findViewById(R.id.publisher_meta);
        btnBack = findViewById(R.id.btn_back);
        btnAccept = findViewById(R.id.btn_accept_task);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        // 接收任务ID
        int taskId = getIntent().getIntExtra("task_id", 0);

        // 从网络加载真实数据
        loadTaskData(taskId, new TaskLoadCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                runOnUiThread(() -> {
                    tvCategory.setText(task.getCategory());
                    tvTitle.setText(task.getTitle());
                    tvDescription.setText(task.getDescription());
                    tvPrice.setText(task.getFormattedPrice());
                    tvDeadline.setText(task.getRemainingTime());
                    tvPublisherName.setText(task.getPublisherName());
                    tvPublisherMeta.setText(task.getPublisherMeta());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(TaskDetailActivity.this,
                            "加载失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadTaskData(int taskId, TaskLoadCallback callback) {
        TaskHttpClient.taskDetail(taskId, new BaseHttpClient.ApiCallback<>() {
            @Override
            public void onSuccess(BaseHttpClient.TaskData data) {
                Task task = new Task();
                task.setId(data.id);
                task.setTitle(data.title);
                task.setCategory(data.category);
                task.setDescription(data.description);
                task.setPrice(data.reward);
                task.setDeadline(data.deadline);
                task.setPublisherId(data.publisher.id);
                task.setPublisherName(data.publisher.nickname);
                task.setPublisherMeta("信誉积分・" + data.publisher.credit_score);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnAccept.setOnClickListener(v -> {
            // 弹出接单确认框（示例）
             new AlertDialog.Builder(this)
                 .setTitle("确认接单")
                 .setMessage("是否确认接收此任务？")
                 .setPositiveButton("确认", (dialog, which) -> {})
                 .setNegativeButton("取消", null)
                 .show();
        });
    }

    interface TaskLoadCallback {
        void onTaskLoaded(Task task);
        void onError(String error);
    }
}