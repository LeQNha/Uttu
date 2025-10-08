package com.example.uttu.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.BaseActivity
import com.example.uttu.R
import com.example.uttu.adapters.TaskAdapter
import com.example.uttu.databinding.ActivityProjectDetailsBinding
import com.example.uttu.models.Project
import com.example.uttu.models.Task
import com.example.uttu.viewmodels.ProjectViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class ProjectDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityProjectDetailsBinding
    private var currentUserRole: String = "Member" // default

    var project: Project? = null

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_project_details)
        binding = ActivityProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        project = intent.getParcelableExtra<Project>("project")

        project?.let {
            binding.projectName.text = it.projectName
            highlightStatus(it.projectStatus)
            projectViewModel.loadTeamMembers(it.projectId)
        }

        taskRvSetUp()
        onClickLisenerSetUp()
        resultObserve()
        setupStatusCardClick()

    }

    override fun onResume() {
        super.onResume()
        // Refresh lại danh sách project khi quay về fragment
        project?.projectId?.let { projectId ->
            taskViewModel.loadTasks(projectId)
        }
    }

    private fun onClickLisenerSetUp() {
        binding.btnSeeAllTasks.setOnClickListener {
            val intent = Intent(this, SeeAllTasksActivity::class.java)
            intent.putExtra("userRole", currentUserRole) // truyền role
            intent.putExtra("projectId", project?.projectId) // ✅ truyền projectId
            startActivity(intent)
        }

        binding.btnSeeAllMembers.setOnClickListener {
            val intent = Intent(this, SeeAllMembersActivity::class.java)
            intent.putExtra("teamId", project?.projectId) // truyền projectId
            intent.putExtra("userRole", currentUserRole) // truyền role
            startActivity(intent)
        }

        binding.btnDeleteProject.setOnClickListener {
            val teamId = project?.projectId ?: return@setOnClickListener
            if (currentUserRole == "Leader") {
                AlertDialog.Builder(this)
                    .setTitle("Delete Project")
                    .setMessage("Do you really want to delete this project?")
                    .setPositiveButton("Yes") { _, _ ->
                        projectViewModel.deleteProject(teamId)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "You can not do this action", Toast.LENGTH_SHORT).show()
            }
        }

        binding.projectName.setOnClickListener {
            if (currentUserRole == "Leader" && project != null) {
                showEditProjectNameDialog(project!!)
            }
        }

        binding.btnBack.setOnClickListener {
            finish() // Kết thúc activity hiện tại và quay lại activity trước đó
        }
    }

    private fun resultObserve(){
        // Lắng nghe kết quả xoá
        projectViewModel.deleteProjectResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Project deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // quay lại màn trước
            }.onFailure { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        projectViewModel.teamMembers.observe(this) { result ->
            result.onSuccess { pair ->
                currentUserRole = pair.second
            }
        }

        projectViewModel.updateProjectNameResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Project name updated successfully", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(this, "Error updating name: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        projectViewModel.updateProjectStatusResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Project status updated", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(this, "Error updating status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.tasks.observe(this) { list ->
            taskList.clear()
            // chỉ lấy vài task gần nhất (ví dụ 3 task) nếu muốn giống dashboard
            taskList.addAll(list.take(3))
            taskAdapter.notifyDataSetChanged()
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
                createdAt = Date(System.currentTimeMillis() - 1 * 60 * 60 * 1000),
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
//            intent.putExtra("projectId", project?.projectId) // ✅ truyền projectId
//            startActivity(intent)
//        }
        taskAdapter = TaskAdapter(
            taskList,
            onItemClick = { selectedTask ->
                val intent = Intent(this, TaskDetailsActivity::class.java)
                intent.putExtra("task", selectedTask)
                intent.putExtra("userRole", currentUserRole)
                intent.putExtra("projectId", project?.projectId)
                startActivity(intent)
            },
            onStatusUpdate = { taskId, newStatus ->
                taskViewModel.updateTaskStatus(taskId, newStatus)
            }
        )
        binding.taskRecyclerView.adapter = taskAdapter
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)

        // gọi loadTasks cho project hiện tại
        project?.projectId?.let { projectId ->
            taskViewModel.loadTasks(projectId)
        }
    }

    private fun showEditProjectNameDialog(project: Project) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_project_name, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etProjectName = dialogView.findViewById<TextInputEditText>(R.id.etEditProjectName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        etProjectName.setText(project.projectName)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val newName = etProjectName.text.toString().trim()
            if (newName.isNotEmpty()) {
                projectViewModel.updateProjectName(project.projectId, newName)
                binding.projectName.text = newName
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter project name", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun highlightStatus(status: String) {
        val planningCard = findViewById<MaterialCardView>(R.id.cardViewStatusPlanning)
        val workingCard = findViewById<MaterialCardView>(R.id.cardViewStatusWorking)
        val completedCard = findViewById<MaterialCardView>(R.id.cardViewStatusCompleted)

        // Reset stroke
        val cards = listOf(planningCard, workingCard, completedCard)
        cards.forEach {
            it.strokeWidth = 0
        }

        // Set viền xanh cho card có status hiện tại
        when (status.lowercase()) {
            "planning" -> {
                planningCard.strokeColor = ContextCompat.getColor(this, R.color.main_purple)
                planningCard.strokeWidth = 4
            }
            "working" -> {
                workingCard.strokeColor = ContextCompat.getColor(this, R.color.main_purple)
                workingCard.strokeWidth = 4
            }
            "completed" -> {
                completedCard.strokeColor = ContextCompat.getColor(this, R.color.main_purple)
                completedCard.strokeWidth = 4
            }
        }
    }

    private fun setupStatusCardClick() {
        val planningCard = findViewById<MaterialCardView>(R.id.cardViewStatusPlanning)
        val workingCard = findViewById<MaterialCardView>(R.id.cardViewStatusWorking)
        val completedCard = findViewById<MaterialCardView>(R.id.cardViewStatusCompleted)

        val projectId = project?.projectId ?: return

        val clickListener = { newStatus: String ->
            if (currentUserRole == "Leader") {
                projectViewModel.updateProjectStatus(projectId, newStatus)
                project = project?.copy(projectStatus = newStatus) // cập nhật local object
                highlightStatus(newStatus) // update UI ngay
            } else {
                Toast.makeText(this, "Only Leader can change status", Toast.LENGTH_SHORT).show()
            }
        }

        planningCard.setOnClickListener { clickListener("Planning") }
        workingCard.setOnClickListener { clickListener("Working") }
        completedCard.setOnClickListener { clickListener("Completed") }
    }
}