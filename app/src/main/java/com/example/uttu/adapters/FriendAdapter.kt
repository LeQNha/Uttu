package com.example.uttu.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.databinding.ItemFriendBinding
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.example.uttu.R
import com.example.uttu.models.TeamMember
import com.example.uttu.models.User

class FriendAdapter(
    private var friends: List<User>,
    private val onOptionSelected: (User, Int) -> Unit
): RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    private var originalList: List<User> = friends

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

            btnOption.setOnClickListener { view ->
                showPupupMenu(view, friend, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun updateData(newFriends: List<User>) {
        originalList = newFriends
        friends = newFriends
        notifyDataSetChanged()
    }

    fun removeFriend(userId: String){
        friends = friends.filterNot { it.userId == userId }
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root)

    private fun showPupupMenu(view: View, friend: User, position: Int){
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.menu_friend_options)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_view_profile -> {
                    onOptionSelected(friend, R.id.action_view_profile)
                    true
                }
                R.id.action_unfriend -> {
                    onOptionSelected(friend, R.id.action_unfriend)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    fun filter(query: String) {
        friends = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.username.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}