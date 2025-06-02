package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText verificationCodeEditText;
    private Button getVerificationCodeButton;
    private Button registerButton;
    private TextView backToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        getVerificationCodeButton = findViewById(R.id.getVerificationCodeButton);
        registerButton = findViewById(R.id.registerButton);
        backToLoginText = findViewById(R.id.backToLoginText);
    }

    private void onGetVerificationCode() {
        if (validateInput()) {
            // TODO: 实现注册逻辑
            Toast.makeText(RegisterActivity.this, "验证码获取功能待实现", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // 获取验证码按钮点击事件
        getVerificationCodeButton.setOnClickListener(v -> onGetVerificationCode());
        // 注册按钮点击事件
        registerButton.setOnClickListener(v -> onRegisterClick());
        // 返回登录页面
        backToLoginText.setOnClickListener(v -> onBackToLoginClick());
    }

    private void onRegisterClick() {
        if (validateInput()) {
            // TODO: 实现注册逻辑
            Toast.makeText(RegisterActivity.this, "注册功能待实现", Toast.LENGTH_SHORT).show();
        }
    }

    private void onBackToLoginClick() {
        finish();
    }

    private boolean validateInput() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // 验证邮箱
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("请输入邮箱");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("请输入有效的邮箱地址");
            return false;
        }

        // 验证密码
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("请输入密码");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("密码长度至少为6位");
            return false;
        }

        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("请确认密码");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("两次输入的密码不一致");
            return false;
        }

        return true;
    }
}