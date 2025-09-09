package com.example.uttu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.models.FriendRequested

class FriendRequestedAdapter(
    private val friendRequesteds: List<FriendRequested>,
    private val onAcceptClick: (FriendRequested) -> Unit,
    private val onDeclineClick: (FriendRequested) -> Unit
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
        holder.tvUsername.text = friendRequested.requestSenderId

        holder.btnAccept.setOnClickListener { onAcceptClick(friendRequested) }
        holder.btnDecline.setOnClickListener { onDeclineClick(friendRequested) }
    }

    override fun getItemCount(): Int {
        return friendRequesteds.size
    }

    inner class FriendRequestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val btnAccept: ImageView = itemView.findViewById(R.id.btnAccept)
        val btnDecline: ImageView = itemView.findViewById(R.id.btnDecline)
    }
}