package com.example.school_swap;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.school_swap.network.BaseHttpClient;
import com.example.school_swap.network.TaskHttpClient;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    // 添加分类标签数组
    private TextView[] categoryTabs;
    private int selectedTabIndex = 0; // 默认选中第一个标签
    private EditText searchInput;
    private RecyclerView taskList;
    private TaskAdapter taskAdapter;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 300; // 防抖延迟300毫秒
    private List<Task> allTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);

        // 初始化RecyclerView
        taskList = findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        // 设置适配器并添加点击监听
        taskAdapter = new TaskAdapter(allTasks, taskId -> {
            // 点击任务时跳转到详情页
            Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });
        taskList.setAdapter(taskAdapter);

        // 为发布按钮添加点击事件
        findViewById(R.id.fab_button).setOnClickListener(v -> {
            startActivity(new Intent(this, PublishTaskActivity.class));
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

        // 获取任务数据
        fetchTasks();
    }

    private void fetchTasks() {
        TaskHttpClient.fetchTask(1, 10, new BaseHttpClient.ApiCallback<BaseHttpClient.PaginatedResponse<BaseHttpClient.TaskData>>() {
            @Override
            public void onSuccess(BaseHttpClient.PaginatedResponse<BaseHttpClient.TaskData> data) {
                runOnUiThread(() -> {
                    allTasks.clear();
                    // 转换数据
                    allTasks.addAll(convertToTaskList(data.list));
                    taskAdapter.notifyDataSetChanged();
                    // 加载默认分类数据
                    loadCategoryData(selectedTabIndex);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // 处理错误
                });
            }
        });
    }

    private List<Task> convertToTaskList(List<BaseHttpClient.TaskData> taskDataList) {
        List<Task> tasks = new ArrayList<>();
        if (taskDataList != null) {
            for (BaseHttpClient.TaskData data : taskDataList) {
                Log.d("taskData", data.deadline);
                Task task = new Task();
                task.setId(data.id);
                task.setTitle(data.title);
                task.setDescription(data.description);
                task.setPrice(data.reward);
                task.setCategory(data.category);
                task.setDeadline(data.deadline);
                task.setPublisherName(data.publisher.nickname);
                tasks.add(task);
            }
        }
        return tasks;
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
                    filterTasks(keyword);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        // 设置键盘搜索按钮监听
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = searchInput.getText().toString().trim();
                filterTasks(keyword);
                return true;
            }
            return false;
        });
    }

    // 根据关键词过滤任务
    private void filterTasks(String keyword) {
        if (keyword.isEmpty()) {
            // 如果搜索框为空，显示当前分类的任务
            loadCategoryData(selectedTabIndex);
            return;
        }

        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getTitle().contains(keyword) ||
                    task.getDescription().contains(keyword)) {
                filteredTasks.add(task);
            }
        }

        // 更新RecyclerView
        taskAdapter = new TaskAdapter(filteredTasks, taskId -> {
            Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });
        taskList.setAdapter(taskAdapter);
    }

    // 初始化分类标签并设置点击事件
    private void initCategoryTabs() {
        // 获取所有分类标签
        categoryTabs = new TextView[]{
                findViewById(R.id.category_tab_all),
                findViewById(R.id.category_tab_life),
                findViewById(R.id.category_tab_study),
                findViewById(R.id.category_tab_skill),
                findViewById(R.id.category_tab_other)
        };

        // 设置点击事件
        for (int i = 0; i < categoryTabs.length; i++) {
            final int index = i;
            categoryTabs[i].setOnClickListener(v -> {
                switchCategoryTab(index);
                scrollToTab(index); // 添加滚动到标签
                loadCategoryData(index);
            });
        }
    }

    // 平滑滚动到指定标签
    private void scrollToTab(int position) {
        TextView selectedTab = categoryTabs[position];
        int scrollPos = selectedTab.getLeft() - (getResources().getDisplayMetrics().widthPixels / 2 - selectedTab.getWidth() / 2);
        findViewById(R.id.category_tabs).scrollTo(scrollPos, 0);
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
        List<Task> tasks;
        switch (categoryIndex) {
            case 0: // 全部
                tasks = allTasks;
                break;
            case 1: // 生活类
                tasks = filterTasksByCategory("生活类");
                break;
            case 2: // 学习类
                tasks = filterTasksByCategory("学习类");
                break;
            case 3: // 技能类
                tasks = filterTasksByCategory("技能类");
                break;
            case 4: // 其他
                tasks = filterTasksByCategory("其他");
                break;
            default:
                tasks = allTasks;
        }

        // 更新RecyclerView
        taskAdapter = new TaskAdapter(tasks, taskId -> {
            Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });
        taskList.setAdapter(taskAdapter);
    }

    private List<Task> filterTasksByCategory(String category) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getCategory().equals(category)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    // 任务适配器
    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<Task> taskList;
        private final OnItemClickListener listener;

        // 添加点击监听接口
        public interface OnItemClickListener {
            void onItemClick(int taskId);
        }

        public TaskAdapter(List<Task> taskList, OnItemClickListener listener) {
            this.taskList = taskList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_card, parent, false);
            return new TaskViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskTitle.setText(task.getTitle());
            holder.taskCategory.setText(task.getCategory());
            holder.taskPrice.setText(task.getFormattedPrice());
            holder.taskDescription.setText(task.getDescription());
            holder.taskMeta.setText(task.getPublisherName());
            holder.taskDeadline.setText(task.getRemainingTime());

            // 设置点击事件
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(task.getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        static class TaskViewHolder extends RecyclerView.ViewHolder {
            TextView taskTitle;
            TextView taskCategory;
            TextView taskPrice;
            TextView taskDescription;
            TextView taskMeta;
            TextView taskDeadline;

            TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                taskTitle = itemView.findViewById(R.id.task_title);
                taskCategory = itemView.findViewById(R.id.task_category);
                taskPrice = itemView.findViewById(R.id.task_price);
                taskDeadline = itemView.findViewById(R.id.task_deadline);
                taskDescription = itemView.findViewById(R.id.task_description);
                taskMeta = itemView.findViewById(R.id.task_meta);
            }
        }
    }

    // 设置导航栏点击事件
    private void setupTabBar() {
        // 获取导航栏的按钮
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView taskIcon = findViewById(R.id.task_icon);
        ImageView messageIcon = findViewById(R.id.message_icon);
        ImageView userIcon = findViewById(R.id.user_icon);

        // 设置当前页面为选中状态
        taskIcon.setImageResource(R.drawable.task_active);

        // 设置点击事件
        homeIcon.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        taskIcon.setOnClickListener(v -> {
            // 已经在任务页面，不需要处理
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