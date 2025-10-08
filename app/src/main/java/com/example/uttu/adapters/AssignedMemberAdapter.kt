package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.databinding.ItemAssignedMemberBinding
import com.example.uttu.databinding.ItemMemberBinding
import com.example.uttu.models.TeamMember
import com.example.uttu.models.User
import com.google.firebase.firestore.core.View

class AssignedMemberAdapter(
    private var assignedMembers: List<User>,
    private var currentUserRole: String, // thêm role của current user
    private val onOptionSelected: (User, Int) -> Unit
): RecyclerView.Adapter<AssignedMemberAdapter.AssignMemberViewHolder>() {

    private var originalList: List<User> = assignedMembers

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssignedMemberAdapter.AssignMemberViewHolder {
        val binding = ItemAssignedMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignMemberViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AssignedMemberAdapter.AssignMemberViewHolder,
        position: Int
    ) {
        val assignedMember = assignedMembers[position]
        holder.binding.apply {
            tvUsername.text = assignedMember.username

            btnOption.setOnClickListener { view ->
                showPupupMenu(view, assignedMember, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return assignedMembers.size
    }

    inner class AssignMemberViewHolder(val binding: ItemAssignedMemberBinding) : RecyclerView.ViewHolder(binding.root)

    private fun showPupupMenu(view: android.view.View, member: User, position: Int){
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.menu_assigned_member_options)

        // 🔑 Ẩn option nếu current user không phải leader
        if (currentUserRole != "Leader") {
            popup.menu.findItem(R.id.action_unassign_member).isVisible = false
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_unassign_member -> {
                    onOptionSelected(member, R.id.action_unassign_member)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    fun updateCurrentUserRole(role: String) {
        currentUserRole = role
        notifyDataSetChanged()
    }

    fun updateData(newAssignedMembers: List<User>) {
        originalList = newAssignedMembers
        assignedMembers = newAssignedMembers
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        assignedMembers = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.username.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}