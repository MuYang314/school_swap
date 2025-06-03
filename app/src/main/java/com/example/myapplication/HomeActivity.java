package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 初始化RecyclerView
        RecyclerView productGrid = findViewById(R.id.product_grid);
        productGrid.setLayoutManager(new GridLayoutManager(this, 2));
        ProductAdapter productAdapter = new ProductAdapter(getDummyProducts());
        productGrid.setAdapter(productAdapter);

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置导航栏点击事件
        setupTabBar();
    }

    // 创建虚拟商品数据
    private List<Product> getDummyProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("iPhone 12 128GB 白色 95新",3999, "信息学院", "2分钟前"));
        products.add(new Product("计算机网络教材 第7版", 20, "计算机学院", "5分钟前"));
        products.add(new Product("运动鞋", 200, "体育学院", "10分钟前"));
        products.add(new Product("运动裤",  100, "体育学院", "15分钟前"));
        products.add(new Product("运动袜",  50, "体育学院", "20分钟前"));
        return products;
    }

    // 商品适配器
    static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private final List<Product> productList;

        ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.productTitle.setText(product.getTitle());
            holder.productPrice.setText("¥" + product.getPrice());
            holder.productMeta.setText(product.getMeta());
            holder.productTime.setText(product.getTime());
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        static class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView productTitle;
            TextView productPrice;
            TextView productMeta;
            TextView productTime;
            ImageView productImage;

            ProductViewHolder(View itemView) {
                super(itemView);
                productTitle = itemView.findViewById(R.id.product_title);
                productPrice = itemView.findViewById(R.id.product_price);
                productMeta = itemView.findViewById(R.id.product_meta);
                productTime = itemView.findViewById(R.id.product_time);
                productImage = itemView.findViewById(R.id.product_image);
            }
        }
    }

    // 商品类
    static class Product {
        private final String title;
        private final int price;
        private final String meta;
        private final String time;

        Product(String title, int price, String meta, String time) {
            this.title = title;
            this.price = price;
            this.meta = meta;
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public int getPrice() {
            return price;
        }

        public String getMeta() {
            return meta;
        }

        public String getTime() {
            return time;
        }
    }

    // 设置导航栏点击事件 - 修改为TaskActivity的方式
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

        // 设置点击事件
        homeIcon.setOnClickListener(v -> {
            // 已经在首页，不需要处理
        });

        taskIcon.setOnClickListener(v -> {
            Intent taskIntent = new Intent(this, TaskActivity.class);
            startActivity(taskIntent);
            finish();
        });

//        messageIcon.setOnClickListener(v -> {
//            Intent messageIntent = new Intent(this, MessageActivity.class);
//            startActivity(messageIntent);
//            finish();
//        });

//        userIcon.setOnClickListener(v -> {
//            Intent userIntent = new Intent(this, UserActivity.class);
//            startActivity(userIntent);
//            finish();
//        });
    }
}