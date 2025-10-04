package com.example.uttu.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.TodoAdapter
import com.example.uttu.models.Task
import com.example.uttu.databinding.ActivityTaskDetailsBinding
import com.example.uttu.models.Project
import com.example.uttu.models.Todo
import com.example.uttu.ui.fragments.AddTodoBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Date

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding
    private var task : Task? = null
    private var projectId: String? = null
    private var currentUserRole: String = "Member" // hoặc "Leader"

    val sampleTodos = mutableListOf(
        Todo("1", "task1", "Research pattern flow", "Nghiên cứu pattern...", false, Date()),
        Todo("2", "task1", "Create some option for User Flow", "Viết một số option cho flow...", true, Date()),
        Todo("3", "task1", "Create Rapid Prototype", "Tạo prototype nhanh...", false, Date())
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_task_details)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        task = intent.getParcelableExtra<Task>("task")
        projectId = intent.getStringExtra("projectId")
        currentUserRole = intent.getStringExtra("userRole") ?: "Member"
        println("--- receive in TaskDetailsActivity ${projectId}")

        task?.let {
            binding.tvTaskTitle.text = it.taskTitle
            binding.tvTaskDescription.text = it.taskDescription
            binding.tvStatusTag.text = it.taskStatus
        }

        todoRvSetUp()
        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.btnSeeAllMembers.setOnClickListener {
//            projectId?.let {
//                val intent = Intent(this, SeeAllAssignedMembersActivity::class.java)
//                intent.putExtra("projectId", projectId)  // taskId phải != null
//                startActivity(intent)
//            }
            task?.let { currentTask ->
                val intent = Intent(this, SeeAllAssignedMembersActivity::class.java)
                intent.putExtra("taskId", currentTask.taskId)   // ✅ truyền taskId
                intent.putExtra("userRole", currentUserRole)
                intent.putExtra("projectId", projectId)         // nếu cần dùng projectId
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnProgress.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_task_status, null)
            dialog.setContentView(view)

            val btnAssigned = view.findViewById<LinearLayout>(R.id.btnAssigned)
            val btnInProgress = view.findViewById<LinearLayout>(R.id.btnInProgress)
            val btnCompleted = view.findViewById<LinearLayout>(R.id.btnCompleted)

            val buttons = listOf(btnAssigned, btnInProgress, btnCompleted)

            fun selectButton(selected: LinearLayout, status: String){
                buttons.forEach { it.background = ContextCompat.getDrawable(this, R.drawable.bg_status_unselected) }
                selected.background = ContextCompat.getDrawable(this, R.drawable.bg_status_selected)
                binding.tvStatusTag.text = status
                dialog.dismiss()
            }

            btnAssigned.setOnClickListener { selectButton(btnAssigned, "Assigned") }
            btnInProgress.setOnClickListener { selectButton(btnInProgress, "In Progress") }
            btnCompleted.setOnClickListener { selectButton(btnCompleted, "Completed") }

            dialog.show()
        }

        binding.btnAddTodo.setOnClickListener {
            val addTodoSheet = AddTodoBottomSheet { title, desc ->
                val newTodo = Todo("1", "1", title, desc, false, Date())
                sampleTodos.add(newTodo)
                binding.todoRecyclerView.adapter?.notifyItemInserted(sampleTodos.size -1)
            }

            addTodoSheet.show(supportFragmentManager, "AddTodoBottomSheet")
        }
    }

    private fun todoRvSetUp(){

        val adapter = TodoAdapter(sampleTodos)
        // ⚠️ Quan trọng: phải có LayoutManager
        binding.todoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.todoRecyclerView.adapter = adapter
    }
}