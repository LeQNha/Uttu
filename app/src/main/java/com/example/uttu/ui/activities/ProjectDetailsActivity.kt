package com.example.uttu.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.TaskAdapter
import com.example.uttu.databinding.ActivityProjectDetailsBinding
import com.example.uttu.models.Project
import com.example.uttu.models.Task
import java.util.Date

class ProjectDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_project_details)
        binding = ActivityProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val project = intent.getParcelableExtra<Project>("project")

        project?.let {
            binding.projectName.text = it.projectName
        }

        taskRvSetUp()

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
}