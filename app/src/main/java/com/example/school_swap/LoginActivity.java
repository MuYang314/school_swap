package com.example.school_swap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.school_swap.network.AuthHttpClient;
import com.example.school_swap.network.BaseHttpClient;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 判断用户是否已经登陆
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        if (!token.isEmpty()) {
            navigateToMainActivity();
        }


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
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 验证输入
        if (email.isEmpty()) {
            emailEditText.setError("请输入邮箱");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("请输入有效的邮箱地址");
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("请输入密码");
            return;
        }

        // 显示加载状态
        findViewById(R.id.loginButton).setEnabled(false);
        Toast.makeText(this, "登录中...", Toast.LENGTH_SHORT).show();

        // 调用登录API
        AuthHttpClient.login(email, password, new AuthHttpClient.ApiCallback<>() {
            @Override
            public void onSuccess(BaseHttpClient.BaseResponse<AuthHttpClient.UserData> response) {
                runOnUiThread(() -> {
                    if (response.code == 200) {
                        // 保存用户数据
                        saveUserData(response.data);
                        // 显示成功消息
                        Toast.makeText(LoginActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        // 跳转到主页
                        navigateToMainActivity();
                        // 关闭当前页面
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, response.message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    findViewById(R.id.loginButton).setEnabled(true);
                });
            }
        });
    }

    private void saveUserData(AuthHttpClient.UserData userData) {
        // 使用SharedPreferences保存用户数据
        getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putInt("user_id", userData.id)
            .putString("nickname", userData.nickname)
            .putString("email", userData.email)
            .putString("avatar_url", userData.avatar_url)
            .putInt("credit_score", userData.credit_score)
            .putString("token", userData.token)
            .apply();
    }

    private void navigateToMainActivity() {
        // 跳转到主页面
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}