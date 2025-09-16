package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.databinding.ItemMemberBinding
import com.example.uttu.models.User

class MemberAdapter(
    private val members: List<User>,
    private val onOptionSelected: (User, Int) -> Unit
): RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
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
            tvUsername.text = member.username

            btnOption.setOnClickListener { view ->
                showPupupMenu(view, member, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    inner class MemberViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)

    private fun showPupupMenu(view: View, member: User, position: Int){
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.menu_member_options)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_view_profile -> {
                    onOptionSelected(member, R.id.action_view_member_profile)
                    true
                }
                R.id.action_admin_authorize -> {
                    onOptionSelected(member, R.id.action_admin_authorize)
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
}