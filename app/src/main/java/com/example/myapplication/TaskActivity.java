package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);

        // 初始化RecyclerView
        RecyclerView taskList = findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter taskAdapter = new TaskAdapter(getDummyTasks());
        taskList.setAdapter(taskAdapter);

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置导航栏点击事件
        setupTabBar();
    }

    // 创建虚拟任务数据
    private List<Task> getDummyTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("帮取快递（菜鸟驿站）", "生活类", 15,
                "需要有人帮忙从9号宿舍楼下的菜鸟驿站取一个小件快递，送到14号楼307室，今天下午6点前送达即可。",
                "信息学院 • 张同学", "今天17:00截止"));
        tasks.add(new Task("高数作业辅导（微积分部分）", "学习类", 50,
                "需要一位数学好的同学帮忙辅导高等数学作业，主要是微积分部分，大约需要1-2小时。",
                "工程学院 • 李同学", "明天20:00截止"));
        tasks.add(new Task("社团海报设计（PS/AI）", "技能类", 200,
                "需要设计一张社团招新海报，要求有创意，能够吸引新生。提供社团logo和基本信息，需要会PS或AI。",
                "艺术学院 • 王同学", "3天后截止"));
        return tasks;
    }

    // 任务适配器
    static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<Task> taskList;

        TaskAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_card, parent, false);
            return new TaskViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskTitle.setText(task.getTitle());
            holder.taskCategory.setText(task.getCategory());
            holder.taskPrice.setText("¥" + task.getPrice());
            holder.taskDescription.setText(task.getDescription());
            holder.taskMeta.setText(task.getMeta());
            holder.taskDeadline.setText(task.getDeadline());
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        static class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView taskTitle;
            TextView taskCategory;
            TextView taskPrice;
            TextView taskDescription;
            TextView taskMeta;
            TextView taskDeadline;

            TaskViewHolder(View itemView) {
                super(itemView);
                taskTitle = itemView.findViewById(R.id.task_title);
                taskCategory = itemView.findViewById(R.id.task_category);
                taskPrice = itemView.findViewById(R.id.task_price);
                taskDescription = itemView.findViewById(R.id.task_description);
                taskMeta = itemView.findViewById(R.id.task_meta);
                taskDeadline = itemView.findViewById(R.id.task_deadline);
            }
        }
    }

    // 任务类
    static class Task {
        private final String title;
        private final String category;
        private final int price;
        private final String description;
        private final String meta;
        private final String deadline;

        Task(String title, String category, int price, String description, String meta, String deadline) {
            this.title = title;
            this.category = category;
            this.price = price;
            this.description = description;
            this.meta = meta;
            this.deadline = deadline;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public int getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }

        public String getMeta() {
            return meta;
        }

        public String getDeadline() {
            return deadline;
        }
    }

    // 设置导航栏点击事件
    private void setupTabBar() {
        // 获取导航栏的按钮
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView taskIcon = findViewById(R.id.task_icon);
        ImageView messageIcon = findViewById(R.id.message_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        TextView homeText = findViewById(R.id.home_text);
        TextView taskText = findViewById(R.id.task_text);
        TextView messageText = findViewById(R.id.message_text);
        TextView userText = findViewById(R.id.user_text);

        // 设置点击事件
        homeIcon.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        taskIcon.setOnClickListener(v -> {
            // 已经在任务页面，不需要处理
        });

        messageIcon.setOnClickListener(v -> {
            Intent messageIntent = new Intent(this, MessageActivity.class);
            startActivity(messageIntent);
            finish();
        });

        userIcon.setOnClickListener(v -> {
            Intent userIntent = new Intent(this, UserActivity.class);
            startActivity(userIntent);
            finish();
        });
    }
}