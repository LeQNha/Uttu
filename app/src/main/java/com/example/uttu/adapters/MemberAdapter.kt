package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.databinding.ItemMemberBinding
import com.example.uttu.models.TeamMember
import com.example.uttu.models.User

class MemberAdapter(
    private var members: List<TeamMember>,
    private var currentUserRole: String, // thêm role của current user
    private val onOptionSelected: (TeamMember, Int) -> Unit
): RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private var originalList: List<TeamMember> = members

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberAdapter.MemberViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberAdapter.MemberViewHolder, position: Int) {
        val member = members[position]
        holder.binding.apply {
            tvUsername.text = member.user.username
            tvRole.text = member.role

            btnOption.setOnClickListener { view ->
                showPupupMenu(view, member, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    inner class MemberViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)

    private fun showPupupMenu(view: View, member: TeamMember, position: Int){
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.menu_member_options)

        // 🔑 Ẩn option nếu current user không phải leader
        if (currentUserRole != "Leader") {
            popup.menu.findItem(R.id.action_leader_authorize).isVisible = false
            popup.menu.findItem(R.id.action_delete_member).isVisible = false
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_view_profile -> {
                    onOptionSelected(member, R.id.action_view_member_profile)
                    true
                }
                R.id.action_leader_authorize -> {
                    onOptionSelected(member, R.id.action_leader_authorize)
                    true
                }
                R.id.action_delete_member -> {
                    onOptionSelected(member, R.id.action_delete_member)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    // Hàm updateData để thay danh sách member mới
    fun updateData(newMembers: List<TeamMember>) {
        originalList = newMembers
        members = newMembers
        notifyDataSetChanged()
    }
    fun updateCurrentUserRole(role: String) {
        currentUserRole = role
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        members = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.user.username.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}