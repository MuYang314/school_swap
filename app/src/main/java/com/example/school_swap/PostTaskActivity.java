package com.example.school_swap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.school_swap.network.HttpClient;

import org.json.JSONObject;

import java.util.Objects;

public class PostTaskActivity extends AppCompatActivity {

    private EditText taskTitleEditText;
    private Spinner taskCategorySpinner;
    private EditText taskBudgetEditText;
    private EditText taskDeadlineEditText;
    private EditText taskDescriptionEditText;
    private EditText contactInfoEditText;
    private Button publishButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_publish);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        taskTitleEditText = findViewById(R.id.task_title);
        taskCategorySpinner = findViewById(R.id.task_category);
        taskBudgetEditText = findViewById(R.id.task_budget);
        taskDeadlineEditText = findViewById(R.id.task_deadline);
        taskDescriptionEditText = findViewById(R.id.task_description);
        contactInfoEditText = findViewById(R.id.contact_info);
        publishButton = findViewById(R.id.publish_task_button);
        backButton = findViewById(R.id.back_button);
    }

    private void setupClickListeners() {
        // 返回按钮点击事件
        backButton.setOnClickListener(v -> finish());

        // 发布按钮点击事件
        publishButton.setOnClickListener(v -> onPublishClick());
    }

    private void onPublishClick() {
        String title = taskTitleEditText.getText().toString().trim();
        String category = Objects.requireNonNull(taskCategorySpinner.getSelectedItem()).toString();
        String budgetStr = taskBudgetEditText.getText().toString().trim();
        String deadline = taskDeadlineEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();
        String contactInfo = contactInfoEditText.getText().toString().trim();

        // 验证输入
        if (title.isEmpty()) {
            taskTitleEditText.setError("请输入任务标题");
            return;
        }
        if (budgetStr.isEmpty()) {
            taskBudgetEditText.setError("请输入任务预算");
            return;
        }
        try {
            float budget = Float.parseFloat(budgetStr);
            if (budget <= 0) {
                taskBudgetEditText.setError("预算必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            taskBudgetEditText.setError("请输入有效的预算");
            return;
        }
        if (deadline.isEmpty()) {
            taskDeadlineEditText.setError("请输入截止时间");
            return;
        }
        if (description.isEmpty()) {
            taskDescriptionEditText.setError("请输入任务详情");
            return;
        }
        if (contactInfo.isEmpty()) {
            contactInfoEditText.setError("请输入联系方式");
            return;
        }

        // 显示加载状态
        publishButton.setEnabled(false);
        Toast.makeText(this, "发布中...", Toast.LENGTH_SHORT).show();

        // 构建请求数据
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("category", category);
            json.put("budget", budgetStr);
            json.put("deadline", deadline);
            json.put("description", description);
            json.put("contact_info", contactInfo);

            // 这里应该调用网络请求将数据发送到后端
            // 暂时模拟成功
            Toast.makeText(this, "任务发布成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "请求构建失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            publishButton.setEnabled(true);
        }
    }
}