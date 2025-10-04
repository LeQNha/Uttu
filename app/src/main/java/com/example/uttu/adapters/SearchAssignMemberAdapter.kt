package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemSearchFriendMemberBinding
import com.example.uttu.models.User

class SearchAssignMemberAdapter(
    private var members: List<User>,
    private val onItemClick: (User) -> Unit
): RecyclerView.Adapter<SearchAssignMemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(val binding: ItemSearchFriendMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAssignMemberAdapter.MemberViewHolder {
        val binding = ItemSearchFriendMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchAssignMemberAdapter.MemberViewHolder,
        position: Int
    ) {
        val member = members[position]
        holder.binding.apply {
            tvUsername.text = member.username

            // load avatar (sau có thể dùng Glide/Picasso)
            imgAvatar.setImageResource(com.example.uttu.R.drawable.default_avatar)

            root.setOnClickListener {
                onItemClick(member)
            }
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    fun updateData(newMembers: List<User>) {
        members = newMembers
        notifyDataSetChanged()
    }
}