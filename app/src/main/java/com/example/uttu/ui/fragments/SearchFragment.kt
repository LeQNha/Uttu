package com.example.uttu.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.databinding.FragmentRequestedBinding
import com.example.uttu.databinding.FragmentSearchBinding
import com.example.uttu.models.User
import com.example.uttu.ui.adapters.SearchFriendAdapter

class SearchFragment : Fragment() {

    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!

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

        searchFriendRvSetUp()
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
            users = dummyUsers,
        )
        binding.searchFriendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchFriendRecyclerView.adapter = adapter

    }
}