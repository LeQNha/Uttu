package com.example.uttu.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.ProjectAdapter
import com.example.uttu.databinding.FragmentHomeBinding
import com.example.uttu.models.Project
import com.example.uttu.ui.activities.ProjectDetailsActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val projectList = listOf(
            Project(projectId = "1", projectName = "Project 1", projectDescription = "Do work", projectStatus = "Working"),
            Project(projectId = "2", projectName = "Project 2", projectDescription = "Do work", projectStatus = "Working"),
            Project(projectId = "3", projectName = "Project 3", projectDescription = "Do work", projectStatus = "Working"),
            Project(projectId = "4", projectName = "Project 4", projectDescription = "Do work", projectStatus = "Working"),
            Project(projectId = "5", projectName = "Project 55", projectDescription = "Do work", projectStatus = "Working"),
        )

        val adapter = ProjectAdapter(projectList){ project ->
            val intent = Intent(requireContext(), ProjectDetailsActivity::class.java)
            intent.putExtra("project", project)
            startActivity(intent)
        }
        binding.projectRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.projectRecyclerView.adapter = adapter
    }
}