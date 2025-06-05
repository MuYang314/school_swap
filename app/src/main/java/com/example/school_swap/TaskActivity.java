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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);

        // 初始化RecyclerView
        taskList = findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        // 设置适配器并添加点击监听
        taskAdapter = new TaskAdapter(getDummyTasks(), taskId -> {
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

        List<Task> allTasks;
        switch (selectedTabIndex) {
            case 0: // 全部
                allTasks = getDummyTasks();
                break;
            case 1: // 生活类
                allTasks = getLifeTasks();
                break;
            case 2: // 学习类
                allTasks = getStudyTasks();
                break;
            case 3: // 技能类
                allTasks = getSkillTasks();
                break;
            case 4: // 其他
                allTasks = getOtherTasks();
                break;
            default:
                allTasks = getDummyTasks();
        }

        // 过滤任务
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
                tasks = getDummyTasks();
                break;
            case 1: // 生活类
                tasks = getLifeTasks();
                break;
            case 2: // 学习类
                tasks = getStudyTasks();
                break;
            case 3: // 技能类
                tasks = getSkillTasks();
                break;
            case 4: // 其他
                tasks = getOtherTasks();
                break;
            default:
                tasks = getDummyTasks();
        }

        // 更新RecyclerView
        taskAdapter = new TaskAdapter(tasks, taskId -> {
            Intent intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });
        taskList.setAdapter(taskAdapter);
    }

    // 添加不同分类的数据获取方法
    private List<Task> getLifeTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "帮取快递（菜鸟驿站）", "生活类", 15,
                "需要有人帮忙从9号宿舍楼下的菜鸟驿站取一个小件快递，送到14号楼307室，今天下午6点前送达即可。",
                "信息学院 • 张同学", "今天17:00截止"));
        tasks.add(new Task(2, "代买午餐", "生活类", 10,
                "帮忙从食堂带一份午餐到图书馆3楼自习区，要求12点前送达。",
                "文学院 • 王同学", "今天12:00截止"));
        return tasks;
    }

    private List<Task> getStudyTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(3, "高数作业辅导（微积分部分）", "学习类", 50,
                "需要一位数学好的同学帮忙辅导高等数学作业，主要是微积分部分，大约需要1-2小时。",
                "工程学院 • 李同学", "明天20:00截止"));
        tasks.add(new Task(4, "英语口语陪练", "学习类", 80,
                "寻找英语口语好的同学每周陪练2次，每次1小时，帮助提高口语水平。",
                "外语学院 • 赵同学", "长期有效"));
        return tasks;
    }

    private List<Task> getSkillTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(5, "社团海报设计（PS/AI）", "技能类", 200,
                "需要设计一张社团招新海报，要求有创意，能够吸引新生。提供社团logo和基本信息，需要会PS或AI。",
                "艺术学院 • 王同学", "3天后截止"));
        tasks.add(new Task(6, "小程序开发协助", "技能类", 500,
                "需要一个懂微信小程序开发的同学协助完成毕业设计项目，时间比较灵活。",
                "计算机学院 • 刘同学", "两周内有效"));
        return tasks;
    }

    private List<Task> getOtherTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(7, "校园导游", "其他", 100,
                "需要一位熟悉校园的同学带新生家长参观校园，大约2小时。",
                "招生办 • 李老师", "本周六上午"));
        tasks.add(new Task(8, "活动志愿者", "其他", 0,
                "招募校园开放日活动志愿者，提供志愿者证明和午餐。",
                "团委 • 学生会", "下周日全天"));
        return tasks;
    }

    // 创建虚拟任务数据
    private List<Task> getDummyTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "帮取快递（菜鸟驿站）", "生活类", 15,
                "需要有人帮忙从9号宿舍楼下的菜鸟驿站取一个小件快递，送到14号楼307室，今天下午6点前送达即可。",
                "信息学院 • 张同学", "今天17:00截止"));
        tasks.add(new Task(3, "高数作业辅导（微积分部分）", "学习类", 50,
                "需要一位数学好的同学帮忙辅导高等数学作业，主要是微积分部分，大约需要1-2小时。",
                "工程学院 • 李同学", "明天20:00截止"));
        tasks.add(new Task(5, "社团海报设计（PS/AI）", "技能类", 200,
                "需要设计一张社团招新海报，要求有创意，能够吸引新生。提供社团logo和基本信息，需要会PS或AI。",
                "艺术学院 • 王同学", "3天后截止"));
        return tasks;
    }

    // 任务适配器
    static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<Task> taskList;
        private final OnItemClickListener listener;

        // 添加点击监听接口
        interface OnItemClickListener {
            void onItemClick(int taskId);
        }

        TaskAdapter(List<Task> taskList, OnItemClickListener listener) {
            this.taskList = taskList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_card, parent, false);
            return new TaskViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskTitle.setText(task.getTitle());
            holder.taskCategory.setText(task.getCategory());
            holder.taskPrice.setText("¥" + task.getPrice());
            holder.taskDescription.setText(task.getDescription());
            holder.taskMeta.setText(task.getMeta());
            holder.taskDeadline.setText(task.getDeadline());

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

            TaskViewHolder(View itemView) {
                super(itemView);
                taskTitle = itemView.findViewById(R.id.task_title);
                taskCategory = itemView.findViewById(R.id.task_category);
                taskPrice = itemView.findViewById(R.id.task_price);
                taskDescription = itemView.findViewById(R.id.task_description);
                taskMeta = itemView.findViewById(R.id.task_meta);
                taskDeadline = itemView.findViewById(R.id.task_deadline);
            }
        }
    }

    // 任务类
    static class Task {
        private final int id;
        private final String title;
        private final String category;
        private final int price;
        private final String description;
        private final String meta;
        private final String deadline;

        Task(int id, String title, String category, int price, String description, String meta, String deadline) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.price = price;
            this.description = description;
            this.meta = meta;
            this.deadline = deadline;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public int getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }

        public String getMeta() {
            return meta;
        }

        public String getDeadline() {
            return deadline;
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