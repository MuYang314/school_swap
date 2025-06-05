package com.example.school_swap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.school_swap.network.HttpClient;

import org.json.JSONObject;

import java.util.Objects;

public class PostProductActivity extends AppCompatActivity {

    private EditText productTitleEditText;
    private Spinner productCategorySpinner;
    private EditText productPriceEditText;
    private EditText productDescriptionEditText;
    private Button publishButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_publish);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        productTitleEditText = findViewById(R.id.product_title);
        productCategorySpinner = findViewById(R.id.product_category);
        productPriceEditText = findViewById(R.id.product_price);
        productDescriptionEditText = findViewById(R.id.product_description);
        publishButton = findViewById(R.id.publish_product_button);
        backButton = findViewById(R.id.back_button);
    }

    private void setupClickListeners() {
        // 返回按钮点击事件
        backButton.setOnClickListener(v -> finish());

        // 发布按钮点击事件
        publishButton.setOnClickListener(v -> onPublishClick());
    }

    private void onPublishClick() {
        String title = productTitleEditText.getText().toString().trim();
        String category = Objects.requireNonNull(productCategorySpinner.getSelectedItem()).toString();
        String priceStr = productPriceEditText.getText().toString().trim();
        String description = productDescriptionEditText.getText().toString().trim();

        // 验证输入
        if (title.isEmpty()) {
            productTitleEditText.setError("请输入商品标题");
            return;
        }
        if (priceStr.isEmpty()) {
            productPriceEditText.setError("请输入商品价格");
            return;
        }
        try {
            float price = Float.parseFloat(priceStr);
            if (price <= 0) {
                productPriceEditText.setError("价格必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            productPriceEditText.setError("请输入有效的价格");
            return;
        }
        if (description.isEmpty()) {
            productDescriptionEditText.setError("请输入商品详情");
            return;
        }

        // 显示加载状态
        publishButton.setEnabled(false);
        Toast.makeText(this, "发布中...", Toast.LENGTH_SHORT).show();

        // 构建请求数据
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("category", category);
            json.put("price", priceStr);
            json.put("description", description);

            // 这里应该调用网络请求将数据发送到后端
            // 暂时模拟成功
            Toast.makeText(this, "商品发布成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "请求构建失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            publishButton.setEnabled(true);
        }
    }
}