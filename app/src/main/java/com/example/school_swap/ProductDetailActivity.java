package com.example.school_swap;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ViewPager2 vpProductImages;
    private LinearLayout llIndicators;
    private TextView tvTitle, tvPrice, tvDescription, tvSellerName, tvSellerMeta;
    private View btnBack, btnContactSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        vpProductImages = findViewById(R.id.vp_product_images);
        llIndicators = findViewById(R.id.ll_indicators);
        tvTitle = findViewById(R.id.tv_product_title);
        tvPrice = findViewById(R.id.tv_product_price);
        tvDescription = findViewById(R.id.tv_product_description);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvSellerMeta = findViewById(R.id.tv_seller_meta);
        btnBack = findViewById(R.id.btn_back);
        btnContactSeller = findViewById(R.id.btn_contact_seller);
    }

    private void initData() {
        // 接收商品ID
        int productId = getIntent().getIntExtra("product_id", 0);

        // 加载模拟数据
        Product product = loadProductData(productId);

        // 绑定轮播图
        ImageAdapter imageAdapter = new ImageAdapter(product.getImageUrls());
        vpProductImages.setAdapter(imageAdapter);

        // 加载数据
        tvPrice.setText(product.getFormattedPrice());
        tvTitle.setText(product.getTitle());
        tvDescription.setText(product.getDescription());
        tvSellerName.setText(product.getSellerName());
        tvSellerMeta.setText(product.getSellerMeta());

        // 初始化指示器
        initIndicators(product.getImageUrls().size());

        // 监听页面变化更新指示器
        vpProductImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });
    }

    // 初始化指示器
    private void initIndicators(int count) {
        llIndicators.removeAllViews();
        for (int i = 0; i < count; i++) {
            View indicator = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == 0 ? 20 : 8,  // 当前选中点宽度20px，其他8px
                    8
            );
            params.setMargins(2, 0, 2, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(i == 0 ?
                    R.drawable.indicator_active : R.drawable.indicator_inactive);
            llIndicators.addView(indicator);
        }
    }

    // 更新指示器状态
    private void updateIndicators(int currentPosition) {
        for (int i = 0; i < llIndicators.getChildCount(); i++) {
            View indicator = llIndicators.getChildAt(i);
            indicator.setBackgroundResource(i == currentPosition ?
                    R.drawable.indicator_active : R.drawable.indicator_inactive);

            // 动态调整宽度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicator.getLayoutParams();
            params.width = i == currentPosition ? 20 : 8;
            indicator.setLayoutParams(params);
        }
    }

    private Product loadProductData(int productId) {
        // 模拟数据
        Product product = new Product();
        product.setId(productId);
        product.setTitle("iPhone 12 128GB 白色 95新");
        product.setPrice(3999.0);
        product.setDescription("2021年购入，无拆修无划痕，原装配件齐全，支持验货");
        product.setImageUrls(List.of("url1", "url2", "url3")); // 实际替换为图片URL
        product.setSellerName("小明同学");
        product.setSellerMeta("信誉积分・100");
        return product;
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnContactSeller.setOnClickListener(v -> {
            // 跳转聊天页（示例）
            // Intent intent = new Intent(this, ChatActivity.class);
            // startActivity(intent);
        });
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private final List<String> imageUrls;

        public ImageAdapter(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            // 加载图片（需使用Glide/Picasso等库）
            // Glide.with(holder.itemView.getContext())
            //     .load(imageUrls.get(position))
            //     .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return imageUrls.size();
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
}