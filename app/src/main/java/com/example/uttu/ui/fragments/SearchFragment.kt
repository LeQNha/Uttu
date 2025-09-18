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
import com.example.uttu.databinding.FragmentRequestedBinding
import com.example.uttu.databinding.FragmentSearchBinding
import com.example.uttu.models.User
import com.example.uttu.ui.adapters.SearchFriendAdapter
import com.example.uttu.viewmodels.ProjectViewModel
import com.example.uttu.viewmodels.UserViewModel

class SearchFragment : Fragment() {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var adapter: SearchFriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search, container, false)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = (requireActivity() as MainActivity).userViewModel

        searchFriendRvSetUp()
        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.btnSearchUser.setOnClickListener {
            val query = binding.searchBox.text.toString().trim()
            if(query.isNotEmpty()){
                userViewModel.searchUsers(query)
            }
        }
    }

    private fun searchFriendRvSetUp(){
        // danh sách user search được
        val dummyUsers = mutableListOf(
            User("1", "Alamgir Hosian", "alamgir@gmail.com"),
            User("2", "Bablu Khan Bablu", "bablu@gmail.com"),
            User("3", "Nguyen Van A", "a@gmail.com")
        )

        // lưu user đã "Add"
        val addedSet = mutableSetOf<String>()

        adapter = SearchFriendAdapter(
            emptyList(),
            onAddClick = {user ->
                val result = adapter.getUserResult(user.userId)
                if (result?.isRequested == true) {
                    userViewModel.cancelFriendRequest(user.userId)
                }else{
                    userViewModel.sendFriendRequest(user.userId)
                }
            }
        )
        binding.searchFriendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchFriendRecyclerView.adapter = adapter

        userViewModel.searchResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess { users ->
                adapter.updateUsers(users)
            }
            result.onFailure { e ->
                Toast.makeText(requireContext(), "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Quan sát kết quả gửi lời mời
        userViewModel.sendRequestResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Request sent!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        userViewModel.cancelRequestResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Request canceled!", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}