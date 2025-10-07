package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ActivityProjectDetailsBinding
import com.example.uttu.databinding.ItemTodoBinding
import com.example.uttu.models.Todo

class TodoAdapter(
    private var todos: List<Todo>,
    private val onStatusChange: (Todo, Boolean) -> Unit
): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoAdapter.TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        val todo = todos[position]
        holder.binding.apply {
            tvTodoTitle.text = todo.todoTitle
            tvTodoDescription.text = todo.todoDescription
            cbTodoStatus.isChecked = todo.todoStatus

            // Expand/collapse
            var isExpanded = false
            btnExpand.setOnClickListener {
                isExpanded = !isExpanded
                tvTodoDescription.visibility = if (isExpanded) View.VISIBLE else View.GONE
                btnExpand.setImageResource(
                    if (isExpanded) com.example.uttu.R.drawable.ic_arrow_up
                    else com.example.uttu.R.drawable.ic_down_arrow
                )
            }
            // Khi check thì đổi trạng thái
            cbTodoStatus.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != todo.todoStatus) {
                    todo.todoStatus = isChecked
                    onStatusChange(todo, isChecked)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    inner class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newTodos: List<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): Todo {
        return todos[position]
    }
}