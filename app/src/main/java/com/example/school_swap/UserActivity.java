package com.example.school_swap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置导航栏点击事件
        setupTabBar();
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

        // 设置当前页面为选中状态
        userIcon.setImageResource(R.drawable.user_active);
        userText.setTextColor(getResources().getColor(R.color.green));

        // 设置点击事件
        homeIcon.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        taskIcon.setOnClickListener(v -> {
            Intent taskIntent = new Intent(this, TaskActivity.class);
            startActivity(taskIntent);
            finish();
        });

        messageIcon.setOnClickListener(v -> {
            Intent messageIntent = new Intent(this, MessageActivity.class);
            startActivity(messageIntent);
            finish();
        });

        userIcon.setOnClickListener(v -> {
            // 已经在用户页面，不需要处理
        });
    }
}