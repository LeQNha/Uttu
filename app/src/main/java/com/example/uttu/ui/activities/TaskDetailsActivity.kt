package com.example.uttu.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
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
import com.example.uttu.adapters.TodoAdapter
import com.example.uttu.models.Task
import com.example.uttu.databinding.ActivityTaskDetailsBinding
import com.example.uttu.models.Project
import com.example.uttu.models.Todo
import com.example.uttu.ui.fragments.AddTodoBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Date

class TaskDetailsActivity : BaseActivity() {

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

            // ✅ hiển thị deadline
            it.taskDue?.let { date ->
                val dateStr = android.text.format.DateFormat.format("dd MMM yyyy", date)
                binding.tvTaskDate.text = dateStr
            }
        }

        todoRvSetUp()
        onClickListenerSetUp()
        viewmodelObserve()
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

            // reset trạng thái viền
            fun highlightCurrentStatus(status: String) {
                buttons.forEach { it.background = ContextCompat.getDrawable(this, R.drawable.bg_status_unselected) }
                when(status){
                    "Assigned" -> btnAssigned.background = ContextCompat.getDrawable(this, R.drawable.bg_status_selected)
                    "In Progress" -> btnInProgress.background = ContextCompat.getDrawable(this, R.drawable.bg_status_selected)
                    "Completed" -> btnCompleted.background = ContextCompat.getDrawable(this, R.drawable.bg_status_selected)
                }
            }

            fun selectButton(selected: LinearLayout, status: String){
//                buttons.forEach { it.background = ContextCompat.getDrawable(this, R.drawable.bg_status_unselected) }
//                selected.background = ContextCompat.getDrawable(this, R.drawable.bg_status_selected)
                highlightCurrentStatus(status)
                binding.tvStatusTag.text = status
                task?.taskStatus = status
                dialog.dismiss()
            }

            // trạng thái hiện tại
            val currentStatus = task?.taskStatus ?: "Open"
            highlightCurrentStatus(currentStatus)

            // rule click
            if(currentStatus == "Open" || currentStatus == "Dued"){
                // chỉ cho phép Completed
                btnAssigned.isEnabled = false
                btnInProgress.isEnabled = false
                btnAssigned.alpha = 0.5f
                btnInProgress.alpha = 0.5f
            }

            btnAssigned.setOnClickListener {
                if(btnAssigned.isEnabled) {
//                    selectButton(btnAssigned, "Assigned")
                    val newStatus = "Completed"
                    selectButton(btnCompleted, newStatus)

                    task?.let { currentTask ->
                        taskViewModel.updateTaskStatus(currentTask.taskId, newStatus)
                    }
                }
            }
            btnInProgress.setOnClickListener {
                if(btnInProgress.isEnabled){
//                    selectButton(btnInProgress, "In Progress")
                    val newStatus = "In Progress"
                    selectButton(btnCompleted, newStatus)

                    task?.let { currentTask ->
                        taskViewModel.updateTaskStatus(currentTask.taskId, newStatus)
                    }
                }
            }
            btnCompleted.setOnClickListener {
//                selectButton(btnCompleted, "Completed")
                val newStatus = "Completed"
                selectButton(btnCompleted, newStatus)

                task?.let { currentTask ->
                    taskViewModel.updateTaskStatus(currentTask.taskId, newStatus)
                }
            }

            dialog.show()
        }

        binding.tvTaskTitle.setOnClickListener {
            if (currentUserRole == "Leader") {
                showEditDialog(true)
            } else {
                Toast.makeText(this, "Only Leader can edit Title", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvTaskDescription.setOnClickListener {
            if (currentUserRole == "Leader") {
                showEditDialog(false)
            } else {
                Toast.makeText(this, "Only Leader can edit Description", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvTaskDate.setOnClickListener {
            if (currentUserRole == "Leader") {
                showDatePickerDialog()
            } else {
                Toast.makeText(this, "Only Leader can edit deadline", Toast.LENGTH_SHORT).show()
            }
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

    private fun viewmodelObserve(){
        taskViewModel.updateTaskStatusResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun todoRvSetUp(){

        val adapter = TodoAdapter(sampleTodos)
        // ⚠️ Quan trọng: phải có LayoutManager
        binding.todoRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.todoRecyclerView.adapter = adapter
    }

    private fun showEditDialog(isTitle: Boolean){
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
        val editText = dialogView.findViewById<EditText>(R.id.editTextValue)

        if (isTitle) {
            editText.setText(task?.taskTitle)
        } else {
            editText.setText(task?.taskDescription)
        }

        AlertDialog.Builder(this)
            .setTitle(if (isTitle) "Edit Title" else "Edit Description")
            .setView(dialogView)
            .setPositiveButton("Save") { d, _ ->
                val newValue = editText.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    task?.let { currentTask ->
                        if (isTitle) {
                            currentTask.taskTitle = newValue
                            binding.tvTaskTitle.text = newValue
                        } else {
                            currentTask.taskDescription = newValue
                            binding.tvTaskDescription.text = newValue
                        }

                        // gọi ViewModel để update DB
                        taskViewModel.updateTaskField(
                            currentTask.taskId,
                            if (isTitle) "taskTitle" else "taskDescription",
                            newValue
                        )
                    }
                }
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .show()
    }

    private fun showDatePickerDialog() {
        val calendar = java.util.Calendar.getInstance()

        // nếu task đã có deadline thì set mặc định
        task?.taskDue?.let { calendar.time = it }

        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val datePicker = android.app.DatePickerDialog(
            this,
            { _, y, m, d ->
                // Khi đã chọn ngày -> mở tiếp TimePicker
                val timeCalendar = java.util.Calendar.getInstance()
                val hour = timeCalendar.get(java.util.Calendar.HOUR_OF_DAY)
                val minute = timeCalendar.get(java.util.Calendar.MINUTE)

                val timePicker = android.app.TimePickerDialog(
                    this,
                    { _, h, min ->
                        val newCalendar = java.util.Calendar.getInstance()
                        newCalendar.set(y, m, d, h, min, 0)

                        val newDate = newCalendar.time
                        val dateStr = android.text.format.DateFormat.format("dd MMM yyyy HH:mm", newDate)

                        binding.tvTaskDate.text = dateStr
                        task?.taskDue = newDate

                        task?.let { currentTask ->
                            taskViewModel.updateTaskField(
                                currentTask.taskId,
                                "taskDue",
                                newDate  // ✅ lưu timestamp (cả ngày + giờ)
                            )

                            // Nếu task đang là "Dued" và chưa Completed thì reset về "Assigned"
                            if (currentTask.taskStatus == "Dued") {
                                currentTask.taskStatus = "Assigned"
                                binding.tvStatusTag.text = "Assigned"
                                taskViewModel.updateTaskField(
                                    currentTask.taskId,
                                    "taskStatus",
                                    "Assigned"
                                )
                            }
                        }

                    },
                    hour,
                    minute,
                    true // 24h format
                )

                timePicker.show()
            },
            year, month, day
        )

        datePicker.show()
    }

}