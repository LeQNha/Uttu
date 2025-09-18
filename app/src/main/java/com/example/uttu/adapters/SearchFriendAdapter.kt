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
import com.example.uttu.models.SearchUserResult
import com.example.uttu.models.User

class SearchFriendAdapter(
    private var users: List<SearchUserResult>,
    private val onAddClick:(User) -> Unit
) : RecyclerView.Adapter<SearchFriendAdapter.SearchFriendViewHolder>() {

    private val addedUsers = mutableSetOf<String>() // lưu userId đã "Added"

    inner class SearchFriendViewHolder(val binding: ItemSearchFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(searchResult: SearchUserResult) {
            val user = searchResult.user
            binding.tvUsername.text = user.username

            // Kiểm tra user đã được add hay chưa
//            val isAdded = addedUsers.contains(user.userId)
            val isAdded = searchResult.isRequested
//            binding.btnAdd.text = if (isAdded) "Added" else "Add"

            // Đổi màu button theo trạng thái
            if (searchResult.isRequested) {
                binding.btnAdd.text = "Added"
                binding.btnAdd.setBackgroundResource(R.drawable.bg_button_added)
//                binding.btnAdd.isEnabled = false  // đã gửi thì disable
            } else {
                binding.btnAdd.text = "Add"
                binding.btnAdd.setBackgroundResource(R.drawable.bg_button_purple_selector)
//                binding.btnAdd.isEnabled = true
//                binding.btnAdd.setOnClickListener {
//                    onAddClick(user)
//                }
            }

            // Cho phép toggle: Add -> Added -> Add
            binding.btnAdd.setOnClickListener {
                if(searchResult.isRequested){
                    // hủy lời mời
                    onAddClick(user)
                    searchResult.isRequested = false

                    binding.btnAdd.text = "Add"
                    binding.btnAdd.setBackgroundResource(R.drawable.bg_button_purple_selector)
                }
                else {
                    // gửi lời mời
                    onAddClick(user)
                    searchResult.isRequested = true

                    binding.btnAdd.text = "Added"
                    binding.btnAdd.setBackgroundResource(R.drawable.bg_button_added)
                }
            }

            // Sự kiện click
//            binding.btnAdd.setOnClickListener {
//                if (isAdded) {
//                    addedUsers.remove(user.userId) // chuyển lại Add
//                } else {
//                    onAddClick(user)
//                    addedUsers.add(user.userId)    // chuyển thành Added
//                }
//                notifyItemChanged(adapterPosition)
//            }
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

    fun updateUsers(newUsers: List<SearchUserResult>) {
//        users.clear()
//        users.addAll(newUsers)
        users = newUsers
        notifyDataSetChanged()
    }

    fun getUserResult(userId: String): SearchUserResult? {
        return users.find { it.user.userId == userId }
    }

}
