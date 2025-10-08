package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.databinding.ItemProjectBinding
import com.example.uttu.models.Project

class ProjectAdapter(
    private var projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var memberCountMap: Map<String, Int> = emptyMap()
    private var userRolesMap: Map<String, String> = emptyMap()

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

    fun updateData(newList: List<Project>) {
        projects = newList
        notifyDataSetChanged()
    }

    inner class ProjectViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(project: Project){
            binding.apply {
                projectName.text = project.projectName
                projectStatus.text = project.projectStatus

                val count = memberCountMap[project.projectId] ?: 0
                memberCount.text = "+$count"

                val role = userRolesMap[project.projectId] ?: "Member"
                if (role == "Leader") {
                    projectIcon.setImageResource(R.drawable.ic_leader)
                } else {
                    projectIcon.setImageResource(R.drawable.ic_member)
                }

                root.setOnClickListener {
                    onItemClick(project)
                }
            }
        }
    }

    fun updateMemberCounts(newMap: Map<String, Int>) {
        memberCountMap = newMap
        notifyDataSetChanged()
    }

    fun updateUserRoles(newMap: Map<String, String>) {
        userRolesMap = newMap
        notifyDataSetChanged()
    }
}
