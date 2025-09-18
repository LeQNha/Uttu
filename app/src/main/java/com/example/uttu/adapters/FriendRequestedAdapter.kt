package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.models.FriendRequested
import com.example.uttu.models.User

class FriendRequestedAdapter(
    private var friendRequesteds: List<User>,
    private val onAcceptClick: (User) -> Unit,
    private val onDeclineClick: (User) -> Unit
): RecyclerView.Adapter<FriendRequestedAdapter.FriendRequestedViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRequestedAdapter.FriendRequestedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_requested, parent, false)
        return FriendRequestedViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FriendRequestedAdapter.FriendRequestedViewHolder,
        position: Int
    ) {
        val friendRequested = friendRequesteds[position]
        holder.tvUsername.text = friendRequested.username

        holder.btnAccept.setOnClickListener { onAcceptClick(friendRequested) }
        holder.btnDecline.setOnClickListener { onDeclineClick(friendRequested) }
    }

    override fun getItemCount(): Int {
        return friendRequesteds.size
    }

    fun updateData(newUsers: List<User>){
        friendRequesteds = newUsers
        notifyDataSetChanged()
    }

    fun removeUser(userId: String) {
        friendRequesteds = friendRequesteds.filterNot { it.userId == userId }
        notifyDataSetChanged()
    }

    inner class FriendRequestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val btnAccept: ImageView = itemView.findViewById(R.id.btnAccept)
        val btnDecline: ImageView = itemView.findViewById(R.id.btnDecline)
    }
}