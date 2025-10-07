package com.example.uttu.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uttu.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddTodoBottomSheet(
    private val onTodoAdded: (String, String) -> Unit
): BottomSheetDialogFragment() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDesc: TextInputEditText
    private lateinit var btnSave: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.dialog_add_todo, container, false)

        etTitle = view.findViewById<TextInputEditText>(R.id.etTodoTitle)
        etDesc = view.findViewById<TextInputEditText>(R.id.etTodoDesc)
        btnSave = view.findViewById<MaterialButton>(R.id.btnSaveTodo)

        onClickListenerSetUp()

        return view
    }

    private fun onClickListenerSetUp(){
        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()

            if (title.isEmpty()) {
                etTitle.error = "Title is required"
                return@setOnClickListener
            }
//            if (title.isNotEmpty()) {
//                onTodoAdded(title, desc)
//                dismiss()
//            }
            onTodoAdded(title, desc)
            dismiss()
        }
    }
}