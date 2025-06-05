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

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

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

        // 获取传递的商品ID
        int productId = getIntent().getIntExtra("product_id", 0);

        // TODO: 根据productId从后端获取商品详情数据
        // 暂时使用虚拟数据代替
        loadProductDetail(productId);
    }

    @SuppressLint("SetTextI18n")
    private void loadProductDetail(int productId) {
        // 这里应该是网络请求获取商品详情
        // 暂时使用虚拟数据演示

        ProductDetail productDetail = getMockProductDetail(productId);

        // 更新UI
        TextView title = findViewById(R.id.product_title);
        TextView price = findViewById(R.id.product_price);
        TextView description = findViewById(R.id.product_description);
        // ... 其他UI组件

        title.setText(productDetail.getTitle());
        price.setText("¥" + productDetail.getPrice());
        description.setText(productDetail.getDescription());
        // ... 设置其他数据
    }

    private ProductDetail getMockProductDetail(int productId) {
        // 根据ID返回不同的虚拟数据
        switch (productId) {
            case 1:
                return new ProductDetail(1, "iPhone 12 128GB 白色 95新", 3999,
                        "去年12月购入，一直有贴膜和壳，无磕碰，电池健康度91%，配件齐全，诚心出售，欢迎咨询。");
            case 2:
                return new ProductDetail(2, "计算机网络教材 第7版", 20,
                        "教材9成新，有少量笔记，不影响阅读使用。");
            // 其他商品...
            default:
                return new ProductDetail(productId, "默认商品", 0, "商品描述");
        }
    }

    // 商品详情数据类
    static class ProductDetail {
        private int id;
        private String title;
        private float price;
        private String description;

        public ProductDetail(int id, String title, float price, String description) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.description = description;
        }

        public String getTitle() {return title;}

        public float getPrice() {return price;}

        public String getDescription() {return description;}
    }
}
