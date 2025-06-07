package com.example.school_swap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.school_swap.network.ProductHttpClient;
//import com.example.school_swap.network.HttpClient;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PublishProductActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 1001;
    private EditText etProductName, etProductDesc, etProductPrice, etProductCount;
    private Spinner spinnerCategory;
    private RecyclerView rvProductImages;
    private List<Uri> imageUris = new ArrayList<>(); // 存储选择的图片路径
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product);

        initViews();
        initSpinner();
        initImageRecyclerView();
        initSubmitButton();
    }

    private void initViews() {
        etProductName = findViewById(R.id.et_product_name);
        etProductDesc = findViewById(R.id.et_product_desc);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductCount = findViewById(R.id.et_product_count);
        spinnerCategory = findViewById(R.id.spinner_category);
        rvProductImages = findViewById(R.id.rv_product_images);
    }

    private void initSpinner() {
        String[] categories = {"数码", "图书", "服装", "运动", "生活"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void initImageRecyclerView() {
        imageAdapter = new ImageAdapter();
        rvProductImages.setLayoutManager(new GridLayoutManager(this, 3));
        rvProductImages.setAdapter(imageAdapter);
    }

    private void initSubmitButton() {
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                submitProduct();
            }
        });
    }

    private boolean validateForm() {
        String name = etProductName.getText().toString().trim();
        String price = etProductPrice.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入商品名称", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (price.isEmpty()) {
            Toast.makeText(this, "请输入商品价格", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void submitProduct() {
        // 数据封装和提交
        String productName = etProductName.getText().toString();
        String productDesc = etProductDesc.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String price = etProductPrice.getText().toString();
        String count = etProductCount.getText().toString();

        Product product = new Product();
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id", 0);

        product.setOwner_id(user_id);
        product.setTitle(productName);
        product.setDescription(productDesc);
        product.setCategory(category);
        product.setPrice(Float.parseFloat(price));
        product.setCount(Integer.parseInt(count));


        ProductHttpClient.publishProduct(this, product, imageUris, new ProductHttpClient.ResponseCallback() {
            @Override
            public void onSuccess(String message) {

            }

            @Override
            public void onError(String error) {

            }
        });

        // 使用 runOnUiThread 方法确保 Toast 在主线程中显示
//        runOnUiThread(() -> {
            Toast.makeText(this, "商品发布成功：" + productName, Toast.LENGTH_SHORT).show();
            finish();
//        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            if (imageUris.size() >= 3) {  // 超过限制提示
                Toast.makeText(this, "最多上传3张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                imageUris.add(selectedImage);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void pickImage() {
        if (imageUris.size() >= 3) { // 保持数量限制
            Toast.makeText(this, "最多上传3张图片", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    // 图片适配器（显示+图标和已选图片）
    class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ADD = 0;
        private static final int TYPE_IMAGE = 1;
        private static final int MAX_IMAGES = 3;  // 最多上传3张

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_upload, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;

            // 设置宽高比1:1
            ViewGroup.LayoutParams params = imageHolder.ivImage.getLayoutParams();
            int size = rvProductImages.getWidth() / 3 - dpToPx(10); // 计算每列宽度
            params.width = size;
            params.height = size;
            imageHolder.ivImage.setLayoutParams(params);

            if (imageUris.size() < MAX_IMAGES && position == imageUris.size()) {
                // 添加按钮点击事件
                imageHolder.ivImage.setOnClickListener(v -> {
                    // 调用 Activity 的 pickImage 方法
                    PublishProductActivity.this.pickImage();
                });
                // 设置添加图标
                imageHolder.ivImage.setImageResource(R.drawable.ic_add);
                imageHolder.ivDelete.setVisibility(View.GONE); // 隐藏删除按钮
                imageHolder.ivImage.setOnClickListener(v -> pickImage());
            } else {
                // 已选图片项
                Glide.with(PublishProductActivity.this)
                        .load(imageUris.get(position))
                        .into(imageHolder.ivImage);
                imageHolder.ivDelete.setVisibility(View.VISIBLE); // 显示删除按钮
                imageHolder.ivDelete.setOnClickListener(v -> {
                    // 删除图片逻辑
                    imageUris.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount()); // 更新后续位置
                });
            }
        }

        // ViewHolder增加删除按钮引用
        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            ImageView ivDelete;

            ImageViewHolder(View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_image);
                ivDelete = itemView.findViewById(R.id.iv_delete);
            }
        }

        // dp转px工具方法（添加在PublishProductActivity中）
        private int dpToPx(int dp) {
            return (int) (dp * getResources().getDisplayMetrics().density);
        }

        @Override
        public int getItemCount() {
            // 已选图片数 < 3时显示添加按钮（总数+1），否则只显示已选图片
            return imageUris.size() < MAX_IMAGES ? imageUris.size() + 1 : MAX_IMAGES;
        }

        @Override
        public int getItemViewType(int position) {
            return position == imageUris.size() ? TYPE_ADD : TYPE_IMAGE;
        }

    }
}