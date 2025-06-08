package com.example.school_swap.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.school_swap.models.Product;
import com.example.school_swap.R;
import com.example.school_swap.network.BaseHttpClient;
import com.example.school_swap.network.ProductHttpClient;
import com.squareup.picasso.Picasso;

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

        // 先初始化空数据防止NPE
        Product product = new Product();
        product.setImageUids(new ArrayList<>()); // 初始化空列表

        // 加载真实数据
        loadProductData(productId, loadedProduct -> {
            // 更新UI数据
            runOnUiThread(() -> {
                tvPrice.setText(loadedProduct.getFormattedPrice());
                tvTitle.setText(loadedProduct.getTitle());
                tvDescription.setText(loadedProduct.getDescription());
                tvSellerName.setText(loadedProduct.getSellerName());
                tvSellerMeta.setText(loadedProduct.getSellerMeta());

                // 更新轮播图数据
                ImageAdapter imageAdapter = new ImageAdapter(
                        loadedProduct.getImageUids() != null ?
                                loadedProduct.getImageUids() :
                                new ArrayList<>()
                );
                // 确保适配器只被设置一次
                if (vpProductImages.getAdapter() != null) {
                    vpProductImages.setAdapter(null); // 移除旧的适配器
                }
                vpProductImages.setAdapter(imageAdapter);

                // 初始化指示器
                initIndicators(imageAdapter.getItemCount());
            });
        });

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

    private void loadProductData(int productId, ProductLoadCallback callback) {
        ProductHttpClient.productDetail(productId, new BaseHttpClient.ApiCallback<>() {
            @Override
            public void onSuccess(ProductHttpClient.ProductData data) {
                Product product = new Product();
                product.setTitle(data.title);
                product.setPrice(data.price);
                product.setDescription(data.description);
                product.setImageUids(data.images != null ? data.images : new ArrayList<>());
                product.setSellerName(data.owner.nickname);
                product.setSellerMeta("信誉积分・" + data.owner.credit_score);

                // 确保在主线程中更新 UI
                runOnUiThread(() -> {
                    callback.onProductLoaded(product);
                });
            }

            @Override
            public void onError(String error) {
                // 错误处理
                runOnUiThread(() -> {
                    Toast.makeText(ProductDetailActivity.this,
                            "加载失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    interface ProductLoadCallback {
        void onProductLoaded(Product product);
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
            Log.d("imageUid", "uid: " + imageUrls.get(position));
            // 加载图片
            Picasso.get()
                    .load("http://192.168.198.76:8000/api/image/" + imageUrls.get(position))
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            if (imageUrls == null) {
                return 0;
            }
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