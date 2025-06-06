package com.example.school_swap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import java.util.Collections;
import java.util.List;

import com.example.school_swap.network.HttpClient;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 初始化RecyclerView
        RecyclerView productGrid = findViewById(R.id.product_grid);
        productGrid.setLayoutManager(new GridLayoutManager(this, 2));

        // 设置适配器并添加点击监听
        productAdapter = new ProductAdapter(getProducts(), productId -> {
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
    }

    private void initSearchView() {
        searchInput = findViewById(R.id.search_input);
        productGrid = findViewById(R.id.product_grid);

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

        List<Product> allProducts;
        switch (selectedTabIndex) {
            case 0: // 全部
                allProducts = getProducts();
                break;
            case 1: // 数码
                allProducts = getDigitalProducts();
                break;
            case 2: // 图书
                allProducts = getBookProducts();
                break;
//            case 3: // 服装
//                allProducts = getClothingProducts();
//                break;
//            case 4: // 运动
//                allProducts = getSportsProducts();
//                break;
//            case 5: // 生活
//                allProducts = getLifeProducts();
//                break;
            // 其他分类...
            default:
                allProducts = getProducts();
        }

        // 过滤商品
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
        // 根据分类索引加载不同数据
        List<Product> products = Collections.emptyList();
        switch (categoryIndex) {
            case 0: // 全部
                products = getProducts();
                break;
            case 1: // 数码
                products = getDigitalProducts();
                break;
            case 2: // 图书
                products = getBookProducts();
                break;
            case 3: // 服装
                products = getClothingProducts();
                break;
            case 4: // 运动
//                products = getSportsProducts();
                break;
            case 5: // 生活
//                products = getLifeProducts();
                break;
            // 其他分类...
            default:
                products = getProducts();
        }

        // 更新RecyclerView
        productAdapter = new ProductAdapter(products, productId -> {
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });
        productGrid.setAdapter(productAdapter);
    }

    // 添加不同分类的虚拟数据方法
    private List<Product> getDigitalProducts() {
        List<Product> products = new ArrayList<>();
//        products.add(new Product(1, "iPhone 12 128GB 白色 95新", 3999, "信息学院", "2分钟前"));
//        products.add(new Product(6, "华为Mate 40 Pro", 4999, "信息学院", "5分钟前"));
        return products;
    }

    private List<Product> getBookProducts() {
        List<Product> products = new ArrayList<>();
//        products.add(new Product(2, "计算机网络教材 第7版", 20, "计算机学院", "5分钟前"));
//        products.add(new Product(7, "高等数学 上册", 15, "数学学院", "10分钟前"));
        return products;
    }

    private  List<Product> getClothingProducts() {
        productAdapter.filter("学院");
        return productAdapter.productList;
    }

    // 获取商品数据
    private List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        HttpClient.fetchProducts(1, 10, new HttpClient.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {

            }

            @Override
            public void onError(String error) {

            }
        });

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
            holder.productTitle.setText(product.getTitle());
            holder.productPrice.setText("¥" + product.getPrice());
//            holder.productMeta.setText(product.getMeta());
            holder.productTime.setText(product.getCreated_at());

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