package com.example.uttu.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemFriendBinding
import android.view.LayoutInflater
import com.example.uttu.models.User

class FriendAdapter(private val friends: List<User>): RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendViewHolder,
        position: Int
    ) {
        val friend = friends[position]
        holder.binding.apply {
            tvUsername.text = friend.username
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class FriendViewHolder(val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root)
}