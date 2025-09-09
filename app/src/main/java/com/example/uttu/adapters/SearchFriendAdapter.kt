package com.example.uttu.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.databinding.ItemSearchFriendBinding
import com.example.uttu.models.User

class SearchFriendAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<SearchFriendAdapter.SearchFriendViewHolder>() {

    private val addedUsers = mutableSetOf<String>() // lưu userId đã "Added"

    inner class SearchFriendViewHolder(val binding: ItemSearchFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUsername.text = user.username

            // Kiểm tra user đã được add hay chưa
            val isAdded = addedUsers.contains(user.userId)
            binding.btnAdd.text = if (isAdded) "Added" else "Add"

            // Đổi màu button theo trạng thái
            if (isAdded) {
                binding.btnAdd.setBackgroundResource(R.drawable.bg_button_added)
            } else {
                binding.btnAdd.setBackgroundResource(R.drawable.bg_button_purple_selector)
            }

            // Sự kiện click
            binding.btnAdd.setOnClickListener {
                if (isAdded) {
                    addedUsers.remove(user.userId) // chuyển lại Add
                } else {
                    addedUsers.add(user.userId)    // chuyển thành Added
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFriendViewHolder {
        val binding = ItemSearchFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchFriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchFriendViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size
}
