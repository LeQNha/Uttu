package com.example.uttu.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
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
import com.example.uttu.models.Todo
import com.example.uttu.ui.fragments.AddTodoBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Date

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding

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

        val task = intent.getParcelableExtra<Task>("task")

        task?.let {
            binding.tvTaskTitle.text = it.taskTitle
            binding.tvTaskDescription.text = it.taskDescription
        }

        todoRvSetUp()
        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.btnSeeAllMembers.setOnClickListener {
            val intent = Intent(this, SeeAllMembersActivity::class.java)
            startActivity(intent)
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