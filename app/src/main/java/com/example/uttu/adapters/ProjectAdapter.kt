package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemProjectBinding
import com.example.uttu.models.Project

class ProjectAdapter(
    private val projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProjectViewHolder,
        position: Int
    ) {
//        val project = projects[position]
//
//        holder.binding.apply {
//            projectName.text = project.projectName
//            projectStatus.text = project.projectStatus
//        }
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    inner class ProjectViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(project: Project){
            binding.apply {
                projectName.text = project.projectName
                projectStatus.text = project.projectStatus

                root.setOnClickListener {
                    onItemClick(project)
                }
            }
        }
    }
}
