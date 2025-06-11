package com.example.school_swap.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.school_swap.R;
import com.example.school_swap.models.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OnOrderActionListener actionListener;

    public interface OnOrderActionListener {
        void onPayClick(Order order);
        void onCancelClick(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderActionListener listener) {
        this.orders = orders;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, orderTitle, price;
        ImageView order_image;
        Button btnCancel, btnPay;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            order_image = itemView.findViewById(R.id.order_image);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderTitle = itemView.findViewById(R.id.order_title);
            price = itemView.findViewById(R.id.order_price);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
            btnPay = itemView.findViewById(R.id.btn_pay_now);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Order order) {
            orderId.setText("订单编号：" + order.getOrderId());
//            Picasso.get()
//                    .load("")
//                    .into(order_image);
            orderStatus.setText(order.getOrderStatus());
            orderTitle.setText(order.getOrderTitle());
            price.setText("¥" + order.getPrice());

            // 根据订单状态显示/隐藏按钮
            switch (order.getOrderStatus()) {
                case "待付款":
                    btnPay.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setText("取消订单");
                    break;
                case "已付款":
                    btnPay.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setText("申请退款");
                    break;
                default:
                    btnPay.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
            }

            // 设置按钮点击事件
            btnPay.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onPayClick(order);
                }
            });

            btnCancel.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onCancelClick(order);
                }
            });
        }
    }
}