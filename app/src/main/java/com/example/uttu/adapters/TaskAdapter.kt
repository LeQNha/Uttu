package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemTaskBinding
import com.example.uttu.models.Task
import java.util.Date
import java.util.concurrent.TimeUnit

class TaskAdapter(
    private val tasks: List<Task>,
    private val onItemClick: (Task) -> Unit,
    private val onStatusUpdate: (taskId: String, newStatus: String) -> Unit
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
//            tvTaskTime.text = "6h"
            tvTaskTime.text = getTimeRemaining(task.taskDue)
//            tvTaskStatus.text = task.taskStatus

            if (isTaskOverdue(task) && task.taskStatus != "Completed") {
                tvTaskStatus.text = "Dued"
                tvTaskStatus.setTextColor(
                    root.context.getColor(android.R.color.holo_red_dark)
                )
                // Cập nhật Firestore nếu chưa update
                if (task.taskStatus != "Dued") {
                    onStatusUpdate(task.taskId, "Dued")
                    task.taskStatus = "Dued" // cập nhật local để RecyclerView refresh
                }
            }else {
                tvTaskStatus.text = task.taskStatus
                tvTaskStatus.setTextColor(
//                    root.context.getColor(android.R.color.black) // hoặc màu mặc định
                    android.graphics.Color.parseColor("#5266fa")
                )
            }
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

    fun getTimeRemaining(dueDate: Date?): String {
        if (dueDate == null) return "0m"

        val now = Date()
        var diff = dueDate.time - now.time

        if (diff <= 0) return "0m"  // đã hết hạn

        val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
        val days = TimeUnit.MILLISECONDS.toDays(diff) % 30
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

        return when {
            months > 0 -> "${months}mth ${days}d"
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    fun isTaskOverdue(task: Task): Boolean {
        val now = Date()
        return task.taskDue?.before(now) == true
    }
}