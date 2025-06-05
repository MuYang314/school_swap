package com.example.school_swap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail);

        // 获取返回按钮并设置点击事件
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 结束当前Activity，返回上一级
                finish();
            }
        });

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取传递的任务ID
        int taskId = getIntent().getIntExtra("task_id", 0);

        // TODO: 根据taskId从后端获取任务详情数据
        // 暂时使用虚拟数据代替
        loadTaskDetail(taskId);
    }

    @SuppressLint("SetTextI18n")
    private void loadTaskDetail(int taskId) {
        // 这里应该是网络请求获取任务详情
        // 暂时使用虚拟数据演示

        TaskDetail taskDetail = getMockTaskDetail(taskId);

        // 更新UI
        TextView title = findViewById(R.id.task_title);
        TextView price = findViewById(R.id.task_price);
        TextView description = findViewById(R.id.task_description);
        TextView category = findViewById(R.id.task_category);
        TextView deadline = findViewById(R.id.task_deadline);
        TextView publisherName = findViewById(R.id.publisher_name);
        TextView publisherMeta = findViewById(R.id.publisher_meta);

        title.setText(taskDetail.getTitle());
        price.setText("¥" + taskDetail.getPrice());
        description.setText(taskDetail.getDescription());
        category.setText(taskDetail.getCategory());
        deadline.setText("截止时间：" + taskDetail.getDeadline());
        publisherName.setText(taskDetail.getPublisherName());
        publisherMeta.setText(taskDetail.getPublisherMeta());
    }

    private TaskDetail getMockTaskDetail(int taskId) {
        // 根据ID返回不同的虚拟数据
        switch (taskId) {
            case 1:
                return new TaskDetail(1, "高数作业辅导（微积分部分）", 50, "学习类",
                        "需要一位数学好的同学帮忙辅导高等数学作业，主要是微积分部分，大约需要1-2小时。我是工程学院大一学生，对微积分部分理解不够透彻，希望能找到一位有耐心的学长/学姐进行一对一辅导。\n\n具体内容：\n- 函数极限与连续性\n- 导数与微分\n- 定积分与不定积分\n\n时间地点可以协商，希望能在本周内完成。",
                        "明天 20:00", "李同学", "工程学院 · 信用良好");
            case 2:
                return new TaskDetail(2, "帮取快递（菜鸟驿站）", 15, "生活类",
                        "需要有人帮忙从9号宿舍楼下的菜鸟驿站取一个小件快递，送到14号楼307室，今天下午6点前送达即可。",
                        "今天 17:00", "张同学", "信息学院 · 好评率98%");
            case 3:
                return new TaskDetail(3, "社团海报设计（PS/AI）", 200, "技能类",
                        "需要设计一张社团招新海报，要求有创意，能够吸引新生。提供社团logo和基本信息，需要会PS或AI。",
                        "3天后", "王同学", "艺术学院 · 信用良好");
            default:
                return new TaskDetail(taskId, "默认任务", 0, "默认分类",
                        "任务描述", "无截止时间", "发布者", "发布者信息");
        }
    }

    // 任务详情数据类
    static class TaskDetail {
        private int id;
        private String title;
        private float price;
        private String category;
        private String description;
        private String deadline;
        private String publisherName;
        private String publisherMeta;

        public TaskDetail(int id, String title, float price, String category, String description,
                          String deadline, String publisherName, String publisherMeta) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.category = category;
            this.description = description;
            this.deadline = deadline;
            this.publisherName = publisherName;
            this.publisherMeta = publisherMeta;
        }

        public String getTitle() { return title; }
        public float getPrice() { return price; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getDeadline() { return deadline; }
        public String getPublisherName() { return publisherName; }
        public String getPublisherMeta() { return publisherMeta; }
    }
}