package com.example.uttu.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.MainActivity
import com.example.uttu.R
import com.example.uttu.adapters.FriendRequestedAdapter
import com.example.uttu.databinding.FragmentFriendsBinding
import com.example.uttu.databinding.FragmentRequestedBinding
import com.example.uttu.models.FriendRequested
import com.example.uttu.models.User
import com.example.uttu.viewmodels.UserViewModel


class RequestedFragment : Fragment() {

    private var _binding: FragmentRequestedBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

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

        userViewModel = (requireActivity() as MainActivity).userViewModel

        friendRequestedRvSetUp()
    }

    private fun friendRequestedRvSetUp(){
//        val dummyList = listOf(
//            FriendRequested("1", "Alamgir Hosian", "hello"),
//            FriendRequested("2", "Bablu Khan Bablu", "hello")
//        )

        val dummyList = listOf(
            User("1", "Alamgir Hosian", "alamgir@gmail.com"),
            User("2", "Bablu Khan Bablu", "bablu@gmail.com"),
            User("3", "Nguyen Van A", "a@gmail.com")
        )

        adapter = FriendRequestedAdapter(
            friendRequesteds = emptyList(),
            onAcceptClick = { friend ->
//                Toast.makeText(requireContext(), "Accepted ${friend.username}", Toast.LENGTH_SHORT).show()
                userViewModel.acceptFriendRequest(friend.userId)
            },
            onDeclineClick = { friend ->
//                Toast.makeText(requireContext(), "Declined ${friend.username}", Toast.LENGTH_SHORT).show()
                userViewModel.declineFriendRequest(friend.userId)
            }
        )

        binding.friendRequestedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRequestedRecyclerView.adapter = adapter

        userViewModel.incomingFriendRequests.observe(viewLifecycleOwner) {result ->
            result.onSuccess { users ->
                adapter.updateData(users)
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.getIncomingFriendRequests()

        userViewModel.declineRequestResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess { senderId ->
                adapter.removeUser(senderId)
                Toast.makeText(requireContext(), "Declined successfully", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.acceptRequestResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess { senderId ->
                adapter.removeUser(senderId)
                Toast.makeText(requireContext(), "Friend added successfully!", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}