package com.example.uttu.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.uttu.R
import com.example.uttu.databinding.FragmentFriendBinding
import com.example.uttu.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class FriendFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_friend, container, false)
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayoutSetUp()
    }

    private fun onClickListenerSetUp(){

    }

    private fun tabLayoutSetUp(){
        val fragments = listOf(
            FriendsFragment(),
            RequestedFragment(),
            SearchFragment()
        )
        val titles = listOf("Friends", "Requested", "Search")

        binding.viewPager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount() = fragments.size

            override fun createFragment(position: Int) = fragments[position]
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}