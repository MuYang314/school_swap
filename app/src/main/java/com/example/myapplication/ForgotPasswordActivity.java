package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText verificationCodeEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button resetButton;
    private Button getVerificationCodeButton;
    private TextView backToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        getVerificationCodeButton = findViewById(R.id.getVerificationCodeButton);
        resetButton = findViewById(R.id.resetButton);
        backToLoginText = findViewById(R.id.backToLoginText);
    }

    private void setupClickListeners() {
        getVerificationCodeButton.setOnClickListener(v -> onGetVerificationCode());
        resetButton.setOnClickListener(v -> onResetClick());
        backToLoginText.setOnClickListener(v -> onBackToLoginClick());
    }

    private void onGetVerificationCode() {
        if (validateInputs()) {
            // TODO: 实现注册逻辑
            Toast.makeText(ForgotPasswordActivity.this, "验证码获取功能待实现", Toast.LENGTH_SHORT).show();
        }
    }

    private void onResetClick() {
        if (validateInputs()) {
            // TODO: 实现实际的密码重置API调用
            // 这里暂时只显示成功消息并返回登录页面
            Toast.makeText(this, "密码重置功能待实现", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // 验证邮箱
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("请输入邮箱");
            emailEditText.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("请输入有效的邮箱地址");
            emailEditText.requestFocus();
            return false;
        }

        // 验证新密码
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("请输入新密码");
            newPasswordEditText.requestFocus();
            return false;
        }
        if (newPassword.length() < 6) {
            newPasswordEditText.setError("密码长度至少为6位");
            newPasswordEditText.requestFocus();
            return false;
        }

        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("请确认新密码");
            confirmPasswordEditText.requestFocus();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("两次输入的密码不一致");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void onBackToLoginClick() {
        finish();
    }
}