package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemTaskBinding
import com.example.uttu.models.Task

class TaskAdapter(
    private val tasks: List<Task>,
    private val onItemClick: (Task) -> Unit
): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskAdapter.TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.apply {
            tvTaskTitle.text = task.taskTitle
            tvTaskDescription.text = task.taskDescription
            tvTaskTime.text = "6h"
            tvTaskStatus.text = task.taskStatus

            // Xử lý click
            root.setOnClickListener {
                onItemClick(task)
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)
}