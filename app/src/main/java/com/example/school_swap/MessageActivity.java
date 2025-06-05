package com.example.school_swap;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);

        // 初始化RecyclerView
        RecyclerView messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        MessageAdapter messageAdapter = new MessageAdapter(getDummyMessages());
        messageList.setAdapter(messageAdapter);

        // 设置沉浸式状态栏
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 设置导航栏点击事件
        setupTabBar();
    }

    // 创建虚拟消息数据
    private List<Message> getDummyMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("李同学", "iPhone还在吗？可以便宜点吗？", "10:30", 2));
        messages.add(new Message("王同学", "好的，那就这么说定了", "昨天", 0));
        messages.add(new Message("张同学", "关于高数辅导的问题想请教", "昨天", 1));
        messages.add(new Message("校园交换平台", "您的任务已被接单", "前天", 0));
        return messages;
    }

    // 消息适配器
    static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private final List<Message> messageList;

        MessageAdapter(List<Message> messageList) {
            this.messageList = messageList;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messageList.get(position);
            holder.messageName.setText(message.getName());
            holder.messageText.setText(message.getText());
            holder.messageTime.setText(message.getTime());

            if (message.getUnreadCount() > 0) {
                holder.messageBadge.setVisibility(View.VISIBLE);
                holder.messageBadge.setText(String.valueOf(message.getUnreadCount()));
            } else {
                holder.messageBadge.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView messageName;
            TextView messageText;
            TextView messageTime;
            TextView messageBadge;

            MessageViewHolder(View itemView) {
                super(itemView);
                messageName = itemView.findViewById(R.id.message_name);
                messageText = itemView.findViewById(R.id.message_text);
                messageTime = itemView.findViewById(R.id.message_time);
                messageBadge = itemView.findViewById(R.id.message_badge);
            }
        }
    }

    // 消息类
    static class Message {
        private final String name;
        private final String text;
        private final String time;
        private final int unreadCount;

        Message(String name, String text, String time, int unreadCount) {
            this.name = name;
            this.text = text;
            this.time = time;
            this.unreadCount = unreadCount;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }

        public String getTime() {
            return time;
        }

        public int getUnreadCount() {
            return unreadCount;
        }
    }

    // 设置导航栏点击事件
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

        // 设置当前页面的图标和文字颜色
        homeText.setTextColor(getResources().getColor(R.color.tab_inactive));
        taskText.setTextColor(getResources().getColor(R.color.tab_inactive));
        messageText.setTextColor(getResources().getColor(R.color.green));
        userText.setTextColor(getResources().getColor(R.color.tab_inactive));

        // 设置点击事件
        homeIcon.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        taskIcon.setOnClickListener(v -> {
            Intent taskIntent = new Intent(this, TaskActivity.class);
            startActivity(taskIntent);
            finish();
        });

        messageIcon.setOnClickListener(v -> {
            // 已经在消息页面，不需要处理
        });

        userIcon.setOnClickListener(v -> {
            Intent userIntent = new Intent(this, UserActivity.class);
            startActivity(userIntent);
            finish();
        });
    }
}