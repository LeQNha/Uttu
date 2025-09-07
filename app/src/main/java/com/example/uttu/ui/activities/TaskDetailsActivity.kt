package com.example.uttu.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.TodoAdapter
import com.example.uttu.models.Task
import com.example.uttu.databinding.ActivityTaskDetailsBinding
import com.example.uttu.models.Todo
import java.util.Date

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailsBinding

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
    }

    private fun todoRvSetUp(){
        val sampleTodos = listOf(
            Todo("1", "task1", "Research pattern flow", "Nghiên cứu pattern...", false, Date()),
            Todo("2", "task1", "Create some option for User Flow", "Viết một số option cho flow...", true, Date()),
            Todo("3", "task1", "Create Rapid Prototype", "Tạo prototype nhanh...", false, Date())
        )
        val adapter = TodoAdapter(sampleTodos)
        // ⚠️ Quan trọng: phải có LayoutManager
        binding.todoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.todoRecyclerView.adapter = adapter
    }
}