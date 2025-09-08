package com.example.uttu.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.TaskAdapter
import com.example.uttu.databinding.ActivitySeeAllTasksBinding
import com.example.uttu.models.Task
import java.util.Date

class SeeAllTasksActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySeeAllTasksBinding

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

        dropdownSetUp()
        taskRvSetUp()
        setupTabs()
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
            // Ví dụ: log hoặc xử lý sort theo lựa chọn
            println("Selected sort: $selected")
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
                projectId = "P1"
            ),
            Task(
                taskId = "T2",
                taskTitle = "API Development",
                taskDescription = "Implement login and register endpoints",
                taskStatus = "Open",
                taskDue = Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000), // 8 giờ sau
                projectId = "P1"
            ),
            Task(
                taskId = "T3",
                taskTitle = "Database Setup",
                taskDescription = "Setup PostgreSQL with initial schema",
                taskStatus = "In Progress",
                taskDue = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), // 1 ngày sau
                projectId = "P2"
            ),
            Task(
                taskId = "T4",
                taskTitle = "Testing Module",
                taskDescription = "Write unit tests for user authentication module",
                taskStatus = "Completed",
                taskDue = Date(System.currentTimeMillis() - 60 * 60 * 1000), // quá hạn 1 giờ
                projectId = "P3"
            )
        )
        val adapter = TaskAdapter(sampleTasks){selectedTask ->
            val intent = Intent(this, TaskDetailsActivity::class.java)
            intent.putExtra("task", selectedTask)
            startActivity(intent)
        }
        binding.taskRecyclerView.adapter = adapter
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
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

                when (tab.id) {
                    R.id.tabAll -> { /* show all todos */ }
                    R.id.tabCompleted -> { /* filter completed */ }
                    R.id.tabIncomplete -> { /* filter incomplete */ }
                }
            }
        }
    }
}