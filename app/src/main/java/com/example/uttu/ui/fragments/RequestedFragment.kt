package com.example.uttu.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.FriendRequestedAdapter
import com.example.uttu.databinding.FragmentFriendsBinding
import com.example.uttu.databinding.FragmentRequestedBinding
import com.example.uttu.models.FriendRequested


class RequestedFragment : Fragment() {

    private var _binding: FragmentRequestedBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendRequestedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_requested, container, false)
        _binding = FragmentRequestedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendRequestedRvSetUp()
    }

    private fun friendRequestedRvSetUp(){
        val dummyList = listOf(
            FriendRequested("1", "Alamgir Hosian", "hello"),
            FriendRequested("2", "Bablu Khan Bablu", "hello")
        )

        adapter = FriendRequestedAdapter(
            friendRequesteds = dummyList,
            onAcceptClick = { friend ->
                Toast.makeText(requireContext(), "Accepted ${friend.requestSenderId}", Toast.LENGTH_SHORT).show()
            },
            onDeclineClick = { friend ->
                Toast.makeText(requireContext(), "Declined ${friend.requestSenderId}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.friendRequestedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRequestedRecyclerView.adapter = adapter
    }

}