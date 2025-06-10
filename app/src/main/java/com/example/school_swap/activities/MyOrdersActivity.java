package com.example.school_swap.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.school_swap.network.BaseHttpClient;
import com.example.school_swap.network.ProductHttpClient;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private OrderAdapter orderAdapter;
    private List<BaseHttpClient.ProductData> allOrders = new ArrayList<>();
    private TextView[] orderTabs;
    private int selectedTabIndex = 0; // 默认选中第一个标签

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // 初始化RecyclerView
        orderList = findViewById(R.id.order_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderList.setLayoutManager(layoutManager);

        // 初始化订单适配器
        orderAdapter = new OrderAdapter(allOrders);
        orderList.setAdapter(orderAdapter);

        // 初始化订单标签
        initOrderTabs();

        // 获取订单数据
        getOrders(1);
    }

    private void initOrderTabs() {
        // 获取所有订单标签
        orderTabs = new TextView[]{
                findViewById(R.id.order_tab_all),
                findViewById(R.id.order_tab_pending_payment),
                findViewById(R.id.order_tab_pending_transaction)
        };

        // 设置点击事件
        for (int i = 0; i < orderTabs.length; i++) {
            final int index = i;
            orderTabs[i].setOnClickListener(v -> {
                switchOrderTab(index);
                // 这里可以添加标签切换后的数据加载逻辑
                loadOrderData(index);
            });
        }
    }

    private void switchOrderTab(int newIndex) {
        // 恢复之前选中的标签样式
        orderTabs[selectedTabIndex].setTextColor(getResources().getColor(android.R.color.darker_gray));

        // 设置新选中的标签样式
        orderTabs[newIndex].setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        // 更新选中索引
        selectedTabIndex = newIndex;
    }

    private void loadOrderData(int tabIndex) {
        // 根据标签索引过滤订单数据
        List<BaseHttpClient.ProductData> filteredOrders = new ArrayList<>();
        switch (tabIndex) {
            case 0: // 全部
                filteredOrders = allOrders;
                break;
            case 1: // 待付款
                // 过滤待付款的订单
                for (BaseHttpClient.ProductData order : allOrders) {
                    if (order.status.equals("pending_payment")) {
                        filteredOrders.add(order);
                    }
                }
                break;
            case 2: // 待交易
                // 过滤待交易的订单
                for (BaseHttpClient.ProductData order : allOrders) {
                    if (order.status.equals("pending_transaction")) {
                        filteredOrders.add(order);
                    }
                }
                break;
            default:
                filteredOrders = allOrders;
        }

        // 更新RecyclerView
        orderAdapter = new OrderAdapter(filteredOrders);
        orderList.setAdapter(orderAdapter);
    }

    private void getOrders(int page) {
        ProductHttpClient.fetchProducts(page, 10, new BaseHttpClient.ApiCallback<BaseHttpClient.PaginatedResponse<BaseHttpClient.ProductData>>() {
            @Override
            public void onSuccess(BaseHttpClient.PaginatedResponse<BaseHttpClient.ProductData> data) {
                runOnUiThread(() -> {
                    allOrders.addAll(data.list);
                    orderAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                // 处理错误
            }
        });
    }
}