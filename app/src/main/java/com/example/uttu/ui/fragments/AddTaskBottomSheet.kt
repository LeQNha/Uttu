package com.example.uttu.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.uttu.R
import com.example.uttu.models.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddTaskBottomSheet(
    private val onTaskAdded: (Task) -> Unit
): BottomSheetDialogFragment() {
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDesc: TextInputEditText
    private lateinit var etDue: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private var dueDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.dialog_add_task, container, false)

        etTitle = view.findViewById(R.id.etTaskTitle)
        etDesc = view.findViewById(R.id.etTaskDesc)
        etDue = view.findViewById(R.id.etTaskDue)
        btnSave = view.findViewById(R.id.btnSaveTask)

        etDue.setOnClickListener {
            pickDateTime()
        }

        // Chọn date + time
        view.findViewById<ImageView>(R.id.ivCalendar).setOnClickListener {
            pickDateTime()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        btnSave.setOnClickListener {
            val task = Task(
                taskId = "",
                taskTitle = etTitle.text.toString(),
                taskDescription = etDesc.text.toString(),
                taskStatus = "Open",
                taskDue = dueDate?.time ?: Date(),
                createdAt = Date(), // thời điểm hiện tại
                projectId = ""
            )
            onTaskAdded(task)
            dismiss()
        }
    }

    private fun pickDateTime() {
        val calendar = Calendar.getInstance()

        // Date Picker
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                // Time Picker
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        dueDate = calendar
                        etDue.setText(
                            SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                Locale.getDefault()
                            ).format(calendar.time)
                        )
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}