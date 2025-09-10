package com.example.uttu.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.ProjectAdapter
import com.example.uttu.databinding.FragmentHomeBinding
import com.example.uttu.models.Project
import com.example.uttu.ui.activities.ProjectDetailsActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val projectList = listOf(
        Project(projectId = "1", projectName = "Project 1", projectStatus = "Working",  createdAt = Date(
            System.currentTimeMillis() - 5 * 60 * 60 * 1000
        )
        ),
        Project(projectId = "2", projectName = "Project 2", projectStatus = "Working", createdAt = Date(System.currentTimeMillis() - 10 * 60 * 60 * 1000)),
        Project(projectId = "3", projectName = "Project 3", projectStatus = "Working", createdAt = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)),
        Project(projectId = "4", projectName = "Project 4", projectStatus = "Working", createdAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)),
        Project(projectId = "5", projectName = "Project 5", projectStatus = "Working", createdAt = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dropdownSetUp()
        projectRvSetUp()
        onClickListenerSetUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClickListenerSetUp(){
        binding.fabAddProject.setOnClickListener {
            println("Fab add project clicked")
            val dialogView = layoutInflater.inflate(R.layout.dialog_create_project, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialog.show()

            dialogView.findViewById<Button>(R.id.btnSubmitProject).setOnClickListener {
                val name = dialogView.findViewById<TextInputEditText>(R.id.etProjectName).text.toString()

                if (name.isNotEmpty()) {
                    // TODO: Thêm Project vào RecyclerView
                    Toast.makeText(requireContext(), "Project Created: $name", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Please enter project name", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnFilter.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.dialog_filter, null)
            dialog.setContentView(view)

            val checkAll = view.findViewById<CheckBox>(R.id.checkAll)
            val checkAdmin = view.findViewById<CheckBox>(R.id.checkAdmin)
            val checkCompleted = view.findViewById<CheckBox>(R.id.checkCompleted)
            val checkWorking = view.findViewById<CheckBox>(R.id.checkWorking)
            val btnApply = view.findViewById<Button>(R.id.btnApplyFilter)

            btnApply.setOnClickListener {
                val selectedFilters = mutableListOf<String>()
                if (checkAll.isChecked) selectedFilters.add("All")
                if (checkAdmin.isChecked) selectedFilters.add("Admin")
                if (checkCompleted.isChecked) selectedFilters.add("Completed")
                if (checkWorking.isChecked) selectedFilters.add("Working")

                // TODO: gọi adapter.filterList(...) hoặc cập nhật projectList
                // ví dụ:
                // adapter.updateData(filteredList)

                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun dropdownSetUp(){
        // Lấy dữ liệu sort từ strings.xml
        val sortOptions = resources.getStringArray(R.array.sort_options)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            sortOptions
        )
        // Gắn adapter vào AutoCompleteTextView
        binding.sortDropdown.setAdapter(adapter)
        // Xử lý sự kiện chọn
        binding.sortDropdown.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            // Ví dụ: log hoặc xử lý sort theo lựa chọn
            println("Selected sort: $selected")
        }
    }

    private fun projectRvSetUp(){

        val adapter = ProjectAdapter(projectList){ project ->
            val intent = Intent(requireContext(), ProjectDetailsActivity::class.java)
            intent.putExtra("project", project)
            startActivity(intent)
        }
        binding.projectRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.projectRecyclerView.adapter = adapter
    }

    fun updateData(newList: List<Project>) {
//        projectList.clear()
//        projectList.addAll(newList)
//        notifyDataSetChanged()
    }
}