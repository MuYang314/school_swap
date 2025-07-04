package com.example.school_swap.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.school_swap.R;

public class UserActivity extends AppCompatActivity {
    TextView tvUserName, tvCreditScore;
    Button login_logout;
    boolean isLogin;
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
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

        // 初始化视图
        tvUserName = findViewById(R.id.user_name);
        tvCreditScore = findViewById(R.id.credit_score);
        login_logout = findViewById(R.id.login_logout);
        login_logout.setText("登录");

        // 判断用户是否已经登录
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        isLogin = !token.isEmpty();
        if (isLogin) {
            login_logout.setBackgroundResource(R.drawable.button_primary);
            login_logout.setText("退出登录");
            // 初始化用户信息
            initUserInfo();
        }

        // 设置点击事件
        login_logout.setOnClickListener(v -> onClick());
        // 在 UserActivity.java 中
        findViewById(R.id.menu_my_orders).setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, MyOrdersActivity.class);
            startActivity(intent);
        });

        // 设置导航栏点击事件
        setupTabBar();
    }

    private void initUserInfo() {
        // 从SharedPreferences中获取用户信息
        String username = sharedPreferences.getString("nickname", "");
        int credit_score = sharedPreferences.getInt("credit_score", 0);
        tvUserName.setText(username);
        tvCreditScore.setText(String.valueOf(credit_score));
    }

    private void onClick() {
        if (isLogin) {
            sharedPreferences.edit().clear().apply();
            login_logout.setBackgroundResource(R.drawable.btn_submit_bg);
            login_logout.setText("登录");
            isLogin = false;
        } else {
            // 跳转到登录页面
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    // 设置导航栏点击事件
    private void setupTabBar() {
        // 获取导航栏的按钮
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView taskIcon = findViewById(R.id.task_icon);
        ImageView messageIcon = findViewById(R.id.message_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        // 设置当前页面为选中状态
        userIcon.setImageResource(R.drawable.user_active);

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