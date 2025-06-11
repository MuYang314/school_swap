package com.example.school_swap.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.school_swap.R;
import com.example.school_swap.adapters.OrderAdapter;
import com.example.school_swap.models.Order;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity implements OrderAdapter.OnOrderActionListener {
    private List<Order> allOrders;
    private OrderAdapter orderAdapter;
    private TextView[] categoryTabs;
    private int selectedTabIndex = 0; // 默认选中第一个标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // 初始化数据
        allOrders = new ArrayList<>();
        allOrders.add(new Order("20240101001", "待付款", "iPhone 12 128GB 白色 95新", 3999.99));
        allOrders.add(new Order("20240101002", "已付款", "MacBook Pro 2020 13寸", 8999.2));
        allOrders.add(new Order("20240101003", "已完成", "AirPods Pro 2代", 1299.99));
        allOrders.add(new Order("20240101004", "已取消", "iPad Air 64GB", 3399.99));
        allOrders.add(new Order("20240101005", "待付款", "Apple Watch Series 7", 2399.88));

        // 初始化RecyclerView和适配器
        RecyclerView orderRecyclerView = findViewById(R.id.order_list);
        orderAdapter = new OrderAdapter(new ArrayList<>(allOrders), this);
        orderRecyclerView.setAdapter(orderAdapter);

        initCategoryTabs();
    }

    // 初始化分类标签并设置点击事件
    private void initCategoryTabs() {
        // 获取所有分类标签
        categoryTabs = new TextView[]{
                findViewById(R.id.tab_all),
                findViewById(R.id.tab_pending_payment),
                findViewById(R.id.tab_paid),
                findViewById(R.id.tab_completed),
                findViewById(R.id.tab_canceled)
        };

        // 设置点击事件
        for (int i = 0; i < categoryTabs.length; i++) {
            final int index = i;
            categoryTabs[i].setOnClickListener(v -> {
                switchCategoryTab(index);
                // 根据标签的tag属性过滤订单
                String statusTag = (String) categoryTabs[index].getTag();
                filterOrders(statusTag);
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

    // 根据状态过滤订单
    private void filterOrders(String status) {
        List<Order> filteredOrders = new ArrayList<>();
        if ("all".equals(status)) {
            filteredOrders.addAll(allOrders);
        } else {
            String statusText = getStatusText(status);
            for (Order order : allOrders) {
                if (statusText.equals(order.getOrderStatus())) {
                    filteredOrders.add(order);
                }
            }
        }
        orderAdapter.updateOrders(filteredOrders);
    }

    // 将tag转换为显示文本
    private String getStatusText(String status) {
        switch (status) {
            case "pending_payment":
                return "待付款";
            case "paid":
                return "已付款";
            case "completed":
                return "已完成";
            case "canceled":
                return "已取消";
            default:
                return "";
        }
    }

    @Override
    public void onPayClick(Order order) {
        // 处理付款逻辑
        // 例如: 跳转到支付页面
    }

    @Override
    public void onCancelClick(Order order) {
        // 处理取消订单逻辑
        // 例如: 显示确认对话框
    }
}
