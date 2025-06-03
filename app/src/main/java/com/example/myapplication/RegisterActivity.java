package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.network.HttpClient;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText verificationCodeEditText;
    private Button getVerificationCodeButton;
    private Button registerButton;
    private TextView backToLoginText;
    private Handler mainHandler;
    private CountDownTimer verificationCodeTimer;
    private static final int COUNTDOWN_TIME = 60000; // 60秒
    private static final int COUNT_DOWN_INTERVAL = 1000; // 1秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化Handler
        mainHandler = new Handler(Looper.getMainLooper());
        
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
        String email = emailEditText.getText().toString().trim();
        
        // 验证邮箱
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("请输入邮箱");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("请输入有效的邮箱地址");
            return;
        }

        // 禁用获取验证码按钮
        getVerificationCodeButton.setEnabled(false);

        // 调用获取验证码API
        HttpClient.getVerificationCode(email, new HttpClient.ResponseCallback() {
            @Override
            public void onSuccess(String message) {
                mainHandler.post(() -> {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    startVerificationCodeTimer();
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    getVerificationCodeButton.setEnabled(true);
                });
            }
        });
    }

    private void startVerificationCodeTimer() {
        if (verificationCodeTimer != null) {
            verificationCodeTimer.cancel();
        }

        verificationCodeTimer = new CountDownTimer(COUNTDOWN_TIME, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                getVerificationCodeButton.setText(secondsRemaining + "秒后重试");
            }

            @Override
            public void onFinish() {
                getVerificationCodeButton.setEnabled(true);
                getVerificationCodeButton.setText("验证码");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (verificationCodeTimer != null) {
            verificationCodeTimer.cancel();
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
        if (!validateInput()) {
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String verificationCode = verificationCodeEditText.getText().toString().trim();

        // 禁用注册按钮，防止重复提交
        registerButton.setEnabled(false);

        // 调用注册API
        HttpClient.register(email, password, confirmPassword, verificationCode, new HttpClient.ResponseCallback() {
            @Override
            public void onSuccess(String message) {
                mainHandler.post(() -> {
                    registerButton.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    // 注册成功，返回登录页面
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    registerButton.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void onBackToLoginClick() {
        finish();
    }

    private boolean validateInput() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String verificationCode = verificationCodeEditText.getText().toString().trim();

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

        // 验证验证码
        if (TextUtils.isEmpty(verificationCode)) {
            verificationCodeEditText.setError("请输入验证码");
            return false;
        }
        if (verificationCode.length() != 4) {
            verificationCodeEditText.setError("验证码长度应为4位");
            return false;
        }

        return true;
    }
}