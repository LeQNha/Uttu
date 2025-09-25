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
import com.example.uttu.adapters.FriendAdapter
import com.example.uttu.databinding.FragmentFriendsBinding
import com.example.uttu.databinding.FragmentHomeBinding
import com.example.uttu.models.User
import com.example.uttu.viewmodels.UserViewModel

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendAdapter
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_friends, container, false)

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = (requireActivity() as MainActivity).userViewModel

        friendsRvSetUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun friendsRvSetUp(){
        val friends = listOf(
            User(
                userId = "1",
                username = "Joe Belfiore",
                email = "joe.belfiore@example.com",
                phoneNumber = "+1 123-456-7890",
                homeAddress = "Seattle, WA"
            ),
            User(
                userId = "2",
                username = "Bill Gates",
                email = "bill.gates@example.com",
                phoneNumber = "+1 222-333-4444",
                homeAddress = "Medina, WA"
            ),
            User(
                userId = "3",
                username = "Mark Zuckerberg",
                email = "mark.zuckerberg@example.com",
                phoneNumber = "+1 555-666-7777",
                homeAddress = "Palo Alto, CA"
            ),
            User(
                userId = "4",
                username = "Marissa Mayer",
                email = "marissa.mayer@example.com",
                phoneNumber = "+1 888-999-0000",
                homeAddress = "San Francisco, CA"
            ),
            User(
                userId = "5",
                username = "Sundar Pichai",
                email = "sundar.pichai@example.com",
                phoneNumber = "+1 101-202-3030",
                homeAddress = "Mountain View, CA"
            ),
            User(
                userId = "6",
                username = "Jeff Bezos",
                email = "jeff.bezos@example.com",
                phoneNumber = "+1 404-505-6060",
                homeAddress = "Medina, WA"
            )
        )

        adapter = FriendAdapter(emptyList()) { user, actionId ->
            when(actionId){
                R.id.action_view_profile -> {
                    // mở trang profile
                    Toast.makeText(requireContext(), "View ${user.username}", Toast.LENGTH_SHORT).show()
                }
                R.id.action_unfriend -> {
                    // xử lý unfriend
//                    Toast.makeText(requireContext(), "Unfriend ${user.username}", Toast.LENGTH_SHORT).show()
                    userViewModel.unfriend(user.userId)
                }
            }

        }
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendsRecyclerView.adapter = adapter

        userViewModel.friends.observe(viewLifecycleOwner) { result ->
            result.onSuccess { users ->
                adapter.updateData(users)
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.getFriends()

        userViewModel.unfriendResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess { friendId ->
                adapter.removeFriend(friendId)
                Toast.makeText(requireContext(), "Unfriended successfully", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}