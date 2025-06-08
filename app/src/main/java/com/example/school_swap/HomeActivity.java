package com.example.school_swap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.school_swap.network.BaseHttpClient;
import com.example.school_swap.network.ProductHttpClient;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {
    // 添加分类标签数组
    private TextView[] categoryTabs;
    private int selectedTabIndex = 0; // 默认选中第一个标签
    private EditText searchInput;
    private RecyclerView productGrid;
    private ProductAdapter productAdapter;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 300; // 防抖延迟300毫秒
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 初始化RecyclerView
        productGrid = findViewById(R.id.product_grid);
        productGrid.setLayoutManager(new GridLayoutManager(this, 2));

        // 初始化商品适配器
        productAdapter = new ProductAdapter(allProducts, productId -> {
            // 点击商品时跳转到详情页
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });
        productGrid.setAdapter(productAdapter);

        // 为发布按钮添加点击事件
        findViewById(R.id.fab_button).setOnClickListener(v -> {
            startActivity(new Intent(this, PublishProductActivity.class));
        });

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置导航栏点击事件
        setupTabBar();
        // 初始化分类标签
        initCategoryTabs();
        // 初始化搜索框
        initSearchView();

        // 获取商品数据
        getProducts();

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int id = sharedPreferences.getInt("user_id", 0);
        String nickname = sharedPreferences.getString("nickname", "");
        String email = sharedPreferences.getString("email", "");
        int credit_score = sharedPreferences.getInt("credit_score", 0);
        String token = sharedPreferences.getString("token", "");

        Log.d("SharedPreferences", "Id: " + id + ", Nickname: " + nickname +
                ", Email: " + email + ", Credit_score: " + credit_score + ", Token: " + token);
    }

    private void initSearchView() {
        searchInput = findViewById(R.id.search_input);

        // 设置文本变化监听
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // 防抖处理
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    filterProducts(keyword);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        // 设置键盘搜索按钮监听
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = searchInput.getText().toString().trim();
                filterProducts(keyword);
                return true;
            }
            return false;
        });
    }

    // 根据关键词过滤商品
    private void filterProducts(String keyword) {
        if (keyword.isEmpty()) {
            // 如果搜索框为空，显示当前分类的商品
            loadCategoryData(selectedTabIndex);
            return;
        }

        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getTitle().contains(keyword)) {
                filteredProducts.add(product);
            }
        }

        // 更新RecyclerView
        productAdapter = new ProductAdapter(filteredProducts, productId -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });
        productGrid.setAdapter(productAdapter);
    }

    // 初始化分类标签并设置点击事件
    private void initCategoryTabs() {
        // 获取所有分类标签
        categoryTabs = new TextView[]{
                findViewById(R.id.category_tab_all),
                findViewById(R.id.category_tab_digital),
                findViewById(R.id.category_tab_books),
                findViewById(R.id.category_tab_clothing),
                findViewById(R.id.category_tab_sports),
                findViewById(R.id.category_tab_lifestyle)
        };

        // 设置点击事件
        for (int i = 0; i < categoryTabs.length; i++) {
            final int index = i;
            categoryTabs[i].setOnClickListener(v -> {
                switchCategoryTab(index);
                // 这里可以添加分类切换后的数据加载逻辑
                loadCategoryData(index);
            });
        }
    }

    // 切换分类标签状态
    private void switchCategoryTab(int newIndex) {
        // 恢复之前选中的标签样式
        categoryTabs[selectedTabIndex].setBackgroundResource(R.drawable.category_tab_background);

        // 设置新选中的标签样式
        categoryTabs[newIndex].setBackgroundResource(R.drawable.category_tab_background_active);

        // 添加点击动画
        categoryTabs[newIndex].animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(100)
                .withEndAction(() -> categoryTabs[newIndex].animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(50)
                        .start())
                .start();

        // 更新选中索引
        selectedTabIndex = newIndex;
    }

    // 加载分类数据
    private void loadCategoryData(int categoryIndex) {
        List<Product> products = new ArrayList<>();
        switch (categoryIndex) {
            case 0: // 全部
                products = allProducts;
                break;
            case 1: // 数码
                products = filterProductsByCategory("数码");
                break;
            case 2: // 图书
                products = filterProductsByCategory("图书");
                break;
            case 3: // 服装
                products = filterProductsByCategory("服装");
                break;
            case 4: // 运动
                products = filterProductsByCategory("运动");
                break;
            case 5: // 生活
                products = filterProductsByCategory("生活");
                break;
            default:
                products = allProducts;
        }

        // 更新RecyclerView
        productAdapter = new ProductAdapter(products, productId -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });
        productGrid.setAdapter(productAdapter);
    }

    private List<Product> filterProductsByCategory(String category) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getCategory().equals(category)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    // 获取商品数据
    private void getProducts() {
        ProductHttpClient.fetchProducts(1, 10, new BaseHttpClient.ApiCallback<>() {
            @Override
            public void onSuccess(BaseHttpClient.BaseResponse<BaseHttpClient.PaginatedResponse<BaseHttpClient.ProductData>> response) {
                if (response.code == 200) {
                    runOnUiThread(() -> {
                        allProducts.clear();
                        // 从 response.data.goods 获取商品列表
                        allProducts.addAll(convertToProductList(response.data.goods));
                        productAdapter = new ProductAdapter(allProducts, productId -> {
                            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                            intent.putExtra("product_id", productId);
                            startActivity(intent);
                        });
                        productGrid.setAdapter(productAdapter);

                        // 可以在这里更新分页信息（如果需要）
                        int totalItems = response.data.total;
                        int totalPages = response.data.pages;
                        int currentPage = response.data.current_page;
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(HomeActivity.this, "获取商品失败: " + response.message, Toast.LENGTH_SHORT).show());
                }
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(HomeActivity.this,
                        "获取商品失败: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    // 转换方法（可选）
    private List<Product> convertToProductList(List<ProductHttpClient.ProductData> productDataList) {
        List<Product> products = new ArrayList<>();
        for (ProductHttpClient.ProductData data : productDataList) {
            Product product = new Product();
            product.setId(data.id);
            product.setTitle(data.title);
            product.setPrice(data.price);
            product.setCategory(data.category);
            product.setImageUids(data.images);
            product.setCreated_at(data.created_at);
            product.setCount(data.count); // 默认值或从data中获取
            products.add(product);
        }
        return products;
    }

    // 商品适配器
    static class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private final List<Product> productList;
        private final List<Product> originalList; // 保存原始数据
        private final OnItemClickListener listener;

        // 添加点击监听接口
        interface OnItemClickListener {
            void onItemClick(int productId);
        }

        ProductAdapter(List<Product> productList, OnItemClickListener listener) {
            this.productList = productList;
            this.originalList = new ArrayList<>(productList);
            this.listener = listener;
        }

        // 添加过滤方法
        @SuppressLint("NotifyDataSetChanged")
        public void filter(String text) {
            productList.clear();
            if (text.isEmpty()) {
                productList.addAll(originalList);
            } else {
                text = text.toLowerCase();
                for (Product item : originalList) {
                    if (item.getTitle().toLowerCase().contains(text)) {
                        productList.add(item);
                    }
                }
            }
            notifyDataSetChanged();
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
            Picasso.get()
                    .load("http://192.168.198.76:8000/api/image/" + product.getImageUids().get(0))
                    .into(holder.productImage);
            holder.productTitle.setText(product.getTitle());
            holder.productPrice.setText("¥" + product.getPrice());
            holder.productCount.setText("数量：" + product.getCount());
            holder.productTime.setText(formatTime(product.getCreated_at()));

            // 设置点击事件
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        static class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView productTitle;
            TextView productPrice;
            TextView productCount;
            TextView productTime;
            ImageView productImage;

            ProductViewHolder(View itemView) {
                super(itemView);
                productTitle = itemView.findViewById(R.id.product_title);
                productPrice = itemView.findViewById(R.id.product_price);
                productCount = itemView.findViewById(R.id.product_count);
                productTime = itemView.findViewById(R.id.product_time);
                productImage = itemView.findViewById(R.id.product_image);
            }
        }
    }

    // 格式化时间的方法
    private static String formatTime(String createdAt) {
        try {
            // 支持两种格式的解析：
            // 1. 带毫秒和时区偏移的格式：2025-06-06T17:19:33.509498+00:00
            // 2. 原始格式：2025-06-06T17:19:33+08:00
            String[] possibleFormats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",  // 带微秒（实际会截断到毫秒）
                    "yyyy-MM-dd'T'HH:mm:ssXXX"         // 原始格式
            };

            Date createdDate = null;
            ParseException lastException = null;

            // 尝试用多种格式解析
            for (String format : possibleFormats) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                    inputFormat.setLenient(false); // 严格模式，避免错误解析
                    createdDate = inputFormat.parse(createdAt);
                    break; // 解析成功则跳出循环
                } catch (ParseException e) {
                    lastException = e;
                }
            }

            if (createdDate == null) {
                throw lastException != null ? lastException : new ParseException("无法解析日期: " + createdAt, 0);
            }

            long createdTime = createdDate.getTime();
            long currentTime = System.currentTimeMillis();
            long diff = currentTime - createdTime;

            if (diff < 60 * 60 * 1000) { // 小于1小时
                long minutes = diff / (60 * 1000);
                return minutes == 0 ? "刚刚" : minutes + "分钟前";
            } else if (diff < 24 * 60 * 60 * 1000) { // 小于24小时
                long hours = diff / (60 * 60 * 1000);
                return hours + "小时前";
            } else {
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return outputFormat.format(createdDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // 如果解析失败，尝试返回原始字符串的可读部分
            try {
                return createdAt.split("T")[0];
            } catch (Exception ex) {
                return createdAt;
            }
        }
    }

    // 设置导航栏点击事件 - 修改为TaskActivity的方式
    private void setupTabBar() {
        // 获取导航栏的按钮
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView taskIcon = findViewById(R.id.task_icon);
        ImageView messageIcon = findViewById(R.id.message_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        // 设置点击事件
        homeIcon.setOnClickListener(v -> {
            // 已经在首页，不需要处理
        });

        taskIcon.setOnClickListener(v -> {
            Intent taskIntent = new Intent(this, TaskActivity.class);
            startActivity(taskIntent);
            finish();
        });

        messageIcon.setOnClickListener(v -> {
            Intent messageIntent = new Intent(this, MessageActivity.class);
            startActivity(messageIntent);
            finish();
        });

        userIcon.setOnClickListener(v -> {
            Intent userIntent = new Intent(this, UserActivity.class);
            startActivity(userIntent);
            finish();
        });
    }
}