package com.example.uttu.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.BaseActivity
import com.example.uttu.R
import com.example.uttu.adapters.TaskAdapter
import com.example.uttu.databinding.ActivitySeeAllTasksBinding
import com.example.uttu.models.Task
import com.example.uttu.ui.fragments.AddTaskBottomSheet
import java.util.Date

class SeeAllTasksActivity : BaseActivity() {

    private lateinit var binding : ActivitySeeAllTasksBinding
    private var currentUserRole: String = "Member" // hoặc "Leader"
    private var projectId: String = "" // sẽ truyền từ ProjectDetailsActivity

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    private var currentSort = "All"
    private var currentTab = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_see_all_tasks)
        binding = ActivitySeeAllTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // lấy role từ Intent
        currentUserRole = intent.getStringExtra("userRole") ?: "Member"
        projectId = intent.getStringExtra("projectId") ?: ""
        Log.d("SeeAllTasks", "--- receive user role = $currentUserRole")

        // chỉ Leader mới được thấy FAB
        if (currentUserRole == "Leader") {
            binding.fabAddTask.show()
        } else {
            binding.fabAddTask.hide()
        }


        dropdownSetUp()
        taskRvSetUp()
        setupTabs()
        onClickListenerSetUp()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Refresh lại danh sách project khi quay về fragment
        taskViewModel.loadTasks(projectId)
        taskViewModel.loadTasksByFilter(projectId, currentSort, currentTab)

    }

    private fun onClickListenerSetUp(){

        binding.fabAddTask.setOnClickListener {
            val bottomSheet = AddTaskBottomSheet{ newTask ->
                // gán projectId cho task
                val taskWithProject = newTask.copy(projectId = projectId)
                taskViewModel.addTask(taskWithProject)

                // cập nhật ngay UI (optimistic update)
//                taskList.add(taskWithProject)
//                taskAdapter.notifyItemInserted(taskList.size - 1)
            }
            bottomSheet.show(supportFragmentManager, "AddTaskBottomSheet")
        }
        // Nút quay lại
        binding.btnBack.setOnClickListener {
            finish() // Kết thúc activity hiện tại và quay lại activity trước đó
        }
    }

    private fun dropdownSetUp(){
        // Lấy dữ liệu sort từ strings.xml
        val sortOptions = resources.getStringArray(R.array.sort_options_2)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            sortOptions
        )
        // Gắn adapter vào AutoCompleteTextView
        binding.sortDropdown.setAdapter(adapter)
        // Xử lý sự kiện chọn
        binding.sortDropdown.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            currentSort = selected
            taskViewModel.loadTasksByFilter(projectId, currentSort, currentTab)
        }
    }

    private fun taskRvSetUp(){
        val sampleTasks = listOf<Task>(
            Task(
                taskId = "T1",
                taskTitle = "Design Homepage",
                taskDescription = "Create wireframe and UI design for homepage",
                taskStatus = "Assigned",
                taskDue = Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000), // 2 giờ sau
                createdAt = Date(System.currentTimeMillis() - 1 * 60 * 60 * 1000), // tạo 1 giờ trước
                projectId = "P1"
            ),
            Task(
                taskId = "T2",
                taskTitle = "API Development",
                taskDescription = "Implement login and register endpoints",
                taskStatus = "Open",
                taskDue = Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000), // 8 giờ sau
                createdAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000), // tạo 2 giờ trước
                projectId = "P1"
            ),
            Task(
                taskId = "T3",
                taskTitle = "Database Setup",
                taskDescription = "Setup PostgreSQL with initial schema",
                taskStatus = "In Progress",
                taskDue = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), // 1 ngày sau
                createdAt = Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000), // tạo 5 giờ trước
                projectId = "P2"
            ),
            Task(
                taskId = "T4",
                taskTitle = "Testing Module",
                taskDescription = "Write unit tests for user authentication module",
                taskStatus = "Completed",
                taskDue = Date(System.currentTimeMillis() - 60 * 60 * 1000), // quá hạn 1 giờ
                createdAt = Date(System.currentTimeMillis() - 10 * 60 * 60 * 1000), // tạo 10 giờ trước
                projectId = "P3"
            )
        )
//        taskAdapter = TaskAdapter(taskList){selectedTask ->
//            val intent = Intent(this, TaskDetailsActivity::class.java)
//            intent.putExtra("task", selectedTask)
//            intent.putExtra("userRole", currentUserRole)
//            intent.putExtra("projectId", projectId)
//            startActivity(intent)
//        }
        taskAdapter = TaskAdapter(
            taskList,
            onItemClick = { selectedTask ->
                taskViewModel.checkUserAccessToTask(selectedTask.taskId, currentUserRole){ hasAccess ->
                    if (hasAccess) {
                        val intent = Intent(this, TaskDetailsActivity::class.java)
                        intent.putExtra("task", selectedTask)
                        intent.putExtra("userRole", currentUserRole)
                        intent.putExtra("projectId", projectId)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "This is not your task", Toast.LENGTH_SHORT).show()
                    }
                }

//                        val intent = Intent(this, TaskDetailsActivity::class.java)
//                        intent.putExtra("task", selectedTask)
//                        intent.putExtra("userRole", currentUserRole)
//                        intent.putExtra("projectId", projectId)
//                        startActivity(intent)

            },
            onStatusUpdate = { taskId, newStatus ->
                taskViewModel.updateTaskStatus(taskId, newStatus)
            }
        )

        binding.taskRecyclerView.adapter = taskAdapter
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)

        // gọi loadTasks
        taskViewModel.loadTasks(projectId)
    }

    private fun setupTabs(){
        val tabs = listOf(binding.tabAll, binding.tabCompleted, binding.tabIncomplete)

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { t ->
                    t.setBackgroundResource(R.drawable.bg_tab_unselected)
                    t.setTextColor(Color.BLACK)
                }
                tab.setBackgroundResource(R.drawable.bg_tab_selected)
                tab.setTextColor(Color.BLUE)

                currentTab = when (tab.id) {
                    R.id.tabAll -> "All"
                    R.id.tabCompleted -> "Completed"
                    R.id.tabIncomplete -> "Incomplete"
                    else -> "All"
                }

                taskViewModel.loadTasksByFilter(projectId, currentSort, currentTab)
            }
        }
    }

    private fun observeViewModel() {
        taskViewModel.addTaskResult.observe(this) { (success, docId) ->
            if (success && docId != null) {
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                taskViewModel.loadTasks(projectId) // reload sau khi add
            } else {
                Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.tasks.observe(this) { list ->
            Log.d("SeeAllTasks", "Tasks loaded from ViewModel: size=${list.size}")
            list.forEach { task ->
                Log.d("SeeAllTasks", "Task: ${task.taskId}, title=${task.taskTitle}, projectId=${task.projectId}, createdAt=${task.createdAt}")
            }

            taskList.clear()
            taskList.addAll(list)
            taskAdapter.notifyDataSetChanged()
        }

        taskViewModel.updateTaskStatusResult.observe(this) { result ->
            result.onSuccess {
                Log.d("SeeAllTasks", "Task status updated successfully")
            }.onFailure { e ->
                Toast.makeText(this, "Failed to update task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.filteredTasks.observe(this) { list ->
            Log.d("SeeAllTasks", "FilteredTasks loaded: ${list.size}")
            list.forEach { task ->
                Log.d("SeeAllTasks", "→ ${task.taskId} | ${task.taskTitle} | ${task.taskStatus}")
            }

            taskList.clear()
            taskList.addAll(list)
            taskAdapter.notifyDataSetChanged()
        }
    }

}