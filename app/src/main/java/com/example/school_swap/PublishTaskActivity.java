package com.example.school_swap;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class PublishTaskActivity extends AppCompatActivity {
    private EditText etTaskTitle, etTaskDesc, etDeadline, etTaskReward;
    private Spinner spinnerTaskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_task);

        initViews();
        initSpinner();
        initDeadlinePicker();
        initSubmitButton();
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.et_task_title);
        etTaskDesc = findViewById(R.id.et_task_desc);
        etDeadline = findViewById(R.id.et_deadline);
        etTaskReward = findViewById(R.id.et_task_reward);
        spinnerTaskType = findViewById(R.id.spinner_task_type);
    }

    private void initSpinner() {
        String[] taskTypes = {"生活类", "学习类", "技能类", "其他"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, taskTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskType.setAdapter(adapter);
    }

    // 截止时间选择器（新增逻辑）
    private void initDeadlinePicker() {
        etDeadline.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        TimePickerDialog timePicker = new TimePickerDialog(
                                PublishTaskActivity.this,
                                (view1, hourOfDay, minute) -> {
                                    String deadline = String.format("%d-%d-%d %d:%d",
                                            year, month+1, dayOfMonth, hourOfDay, minute);
                                    etDeadline.setText(deadline);
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePicker.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });
    }

    private void initSubmitButton() {
        Button btnSubmit = findViewById(R.id.btn_submit_task);
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                submitTask();
            }
        });
    }

    private boolean validateForm() {
        String title = etTaskTitle.getText().toString().trim();
        String reward = etTaskReward.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入任务标题", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reward.isEmpty()) {
            Toast.makeText(this, "请输入悬赏金额", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitTask() {
        // 模拟数据封装和提交
        String taskTitle = etTaskTitle.getText().toString();
        String taskDesc = etTaskDesc.getText().toString();
        String taskType = spinnerTaskType.getSelectedItem().toString();
        String deadline = etDeadline.getText().toString();
        String reward = etTaskReward.getText().toString();

        Task task = new Task(
                taskTitle,
                taskDesc,
                taskType,
                deadline,
                reward
        );

        Toast.makeText(this, "任务发布成功：" + taskTitle, Toast.LENGTH_SHORT).show();
        finish();
    }

    // 任务数据类（示例）
    static class Task {
        String title;
        String description;
        String type;
        String deadline;
        String reward;

        Task(String title, String description, String type, String deadline, String reward) {
            this.title = title;
            this.description = description;
            this.type = type;
            this.deadline = deadline;
            this.reward = reward;
        }
    }
}