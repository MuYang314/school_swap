package com.example.school_swap;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.school_swap.network.AuthHttpClient;
import com.example.school_swap.network.BaseHttpClient;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText verificationCodeEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button resetButton;
    private Button getVerificationCodeButton;
    private TextView backToLoginText;
    private CountDownTimer countDownTimer;
    private final Handler handler = new Handler(Looper.getMainLooper());

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
        String email = emailEditText.getText().toString().trim();

        // 验证邮箱
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("请输入邮箱");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("请输入有效的邮箱地址");
            emailEditText.requestFocus();
            return;
        }

        // 禁用获取验证码按钮
        getVerificationCodeButton.setEnabled(false);

        // 请求验证码
        AuthHttpClient.getVerificationCode(email, new BaseHttpClient.ApiCallback<>() {
            @Override
            public void onSuccess(String message) {
                handler.post(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    startCountDown();
                });
            }

            @Override
            public void onError(String error) {
                handler.post(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                    getVerificationCodeButton.setEnabled(true);
                });
            }
        });
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                handler.post(() -> {
                    getVerificationCodeButton.setText(String.format("%ds后重试", millisUntilFinished / 1000));
                });
            }

            @Override
            public void onFinish() {
                handler.post(() -> {
                    getVerificationCodeButton.setEnabled(true);
                    getVerificationCodeButton.setText("验证码");
                });
            }
        }.start();
    }

    private void onResetClick() {
        if (!validateInputs()) {
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String verificationCode = verificationCodeEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // 验证验证码
        if (TextUtils.isEmpty(verificationCode)) {
            verificationCodeEditText.setError("请输入验证码");
            verificationCodeEditText.requestFocus();
            return;
        }
        if (verificationCode.length() != 4) {
            verificationCodeEditText.setError("请输入6位验证码");
            verificationCodeEditText.requestFocus();
            return;
        }

        // 禁用重置按钮
        resetButton.setEnabled(false);

        // 调用重置密码API
        AuthHttpClient.resetPassword(email, newPassword, confirmPassword, verificationCode, new BaseHttpClient.ApiCallback<>() {
            @Override
            public void onSuccess(String message) {
                handler.post(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    // 重置成功后返回登录页面
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                handler.post(() -> {
                    Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                    resetButton.setEnabled(true);
                });
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}