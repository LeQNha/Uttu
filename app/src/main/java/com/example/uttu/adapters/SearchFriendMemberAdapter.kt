package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemSearchFriendMemberBinding
import com.example.uttu.models.User

class SearchFriendMemberAdapter(
    private var friends: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<SearchFriendMemberAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(val binding: ItemSearchFriendMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemSearchFriendMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.binding.apply {
            tvUsername.text = friend.username

            // load avatar (sau có thể dùng Glide/Picasso)
            imgAvatar.setImageResource(com.example.uttu.R.drawable.default_avatar)

            root.setOnClickListener {
                onItemClick(friend)
            }
        }
    }

    override fun getItemCount(): Int = friends.size

    fun updateData(newFriends: List<User>) {
        friends = newFriends
        notifyDataSetChanged()
    }
}
