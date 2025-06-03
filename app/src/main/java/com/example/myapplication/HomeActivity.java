package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView productGrid;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 初始化RecyclerView
        productGrid = findViewById(R.id.product_grid);
        productGrid.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(getDummyProducts());
        productGrid.setAdapter(productAdapter);

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 创建虚拟商品数据
    private List<Product> getDummyProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("iPhone 12 128GB 白色 95新", "95新，白色，128GB", 3999, "信息学院", "2分钟前"));
        products.add(new Product("计算机网络教材 第7版", "全新未使用", 20, "计算机学院", "5分钟前"));
        products.add(new Product("运动鞋", "全新未使用", 200, "体育学院", "10分钟前"));
        products.add(new Product("运动裤", "全新未使用", 100, "体育学院", "15分钟前"));
        products.add(new Product("运动袜", "全新未使用", 50, "体育学院", "20分钟前"));
        return products;
    }

    // 商品适配器
    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<Product> productList;

        ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_card, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.productTitle.setText(product.getTitle());
            holder.productPrice.setText("¥" + product.getPrice());
            holder.productDescription.setText(product.getDescription());
            holder.productMeta.setText(product.getMeta());
            holder.productTime.setText(product.getTime());
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView productTitle;
            TextView productPrice;
            TextView productDescription;
            TextView productMeta;
            TextView productTime;
            ImageView productImage;

            ProductViewHolder(View itemView) {
                super(itemView);
                productTitle = itemView.findViewById(R.id.product_title);
                productPrice = itemView.findViewById(R.id.product_price);
                productDescription = itemView.findViewById(R.id.product_meta);
                productMeta = itemView.findViewById(R.id.product_meta);
                productTime = itemView.findViewById(R.id.product_time);
                productImage = itemView.findViewById(R.id.product_image);
            }
        }
    }

    // 商品类
    class Product {
        private String title;
        private String description;
        private int price;
        private String meta;
        private String time;

        Product(String title, String description, int price, String meta, String time) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.meta = meta;
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
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
}