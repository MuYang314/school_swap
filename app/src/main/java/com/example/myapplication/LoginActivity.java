package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化视图
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // 设置点击事件
        findViewById(R.id.loginButton).setOnClickListener(v -> onLoginClick());
        findViewById(R.id.registerText).setOnClickListener(v -> onRegisterClick());
        findViewById(R.id.forgotPasswordText).setOnClickListener(v -> onForgotPasswordClick());
        findViewById(R.id.skipText).setOnClickListener(v -> onSkipClick());
    }

    private void onLoginClick() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // TODO: 实现登录逻辑
        Toast.makeText(this, "登录功能待实现", Toast.LENGTH_SHORT).show();
    }

    private void onRegisterClick() {
        // 跳转到注册页面
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void onForgotPasswordClick() {
        // 跳转到忘记密码页面
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void onSkipClick() {
        // TODO: 跳转到主页
        Toast.makeText(this, "跳过功能待实现", Toast.LENGTH_SHORT).show();
    }
}