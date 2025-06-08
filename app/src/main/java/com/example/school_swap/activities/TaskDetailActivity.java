package com.example.school_swap.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.school_swap.R;
import com.example.school_swap.models.Task;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvCategory, tvTitle, tvDescription, tvPrice, tvDeadline;
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
        btnBack = findViewById(R.id.btn_back);
        btnAccept = findViewById(R.id.btn_accept_task);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        // 接收任务ID
        int taskId = getIntent().getIntExtra("extra_id", 0);

        // 加载模拟数据
        Task task = loadTaskData(taskId);

        // 绑定数据
        tvCategory.setText(task.getCategory());
        tvTitle.setText(task.getTitle());
        tvDescription.setText(task.getDescription());
        tvPrice.setText("¥" + task.getPrice());
        tvDeadline.setText(task.getDeadline());
    }

    private Task loadTaskData(int taskId) {
        // 模拟数据
        Task task = new Task();
        task.setId(taskId);
        task.setCategory("生活类");
        task.setTitle("帮忙取快递（东门菜鸟驿站）");
        task.setDescription("3个快递，大箱子1个，小盒子2个，送到3号楼201室");
        task.setPrice(50.0);
        task.setDeadline("今天18:00前");
        return task;
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnAccept.setOnClickListener(v -> {
            // 弹出接单确认框（示例）
//             new AlertDialog.Builder(this)
//                 .setTitle("确认接单")
//                 .setMessage("是否确认接收此任务？")
//                 .setPositiveButton("确认", (dialog, which) -> {})
//                 .setNegativeButton("取消", null)
//                 .show();
        });
    }
}