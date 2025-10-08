package com.example.uttu.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.R
import com.example.uttu.adapters.MemberAdapter
import com.example.uttu.databinding.ActivitySeeAllMembersBinding
import com.example.uttu.models.User
import androidx.core.widget.addTextChangedListener
import com.example.uttu.BaseActivity
import com.example.uttu.adapters.SearchFriendMemberAdapter

class SeeAllMembersActivity : BaseActivity() {

    private lateinit var binding: ActivitySeeAllMembersBinding
    private var teamId: String? = null
    private var currentUserRole: String = "Member" // hoặc "Leader"

    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_see_all_members)
        binding = ActivitySeeAllMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        teamId = intent.getStringExtra("teamId") // lấy projectId truyền sang
        currentUserRole = intent.getStringExtra("userRole") ?: "Member"

        // chỉ Leader mới được thấy FAB
        if (currentUserRole == "Leader") {
            binding.fabAddNewMember.show()
        } else {
            binding.fabAddNewMember.hide()
        }

        onClickListenerSetUp()
        memberRvSetUp()
        searchBoxSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.fabAddNewMember.setOnClickListener {
            showSearchFriendDialog()
        }
        binding.btnBack.setOnClickListener {
            finish() // Kết thúc activity hiện tại và quay lại activity trước đó
        }
    }

    private fun memberRvSetUp(){
        val members = listOf(
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

        adapter = MemberAdapter(emptyList(), "Member"){ member, actionId ->
            when(actionId) {
                R.id.action_view_member_profile -> {
                    Toast.makeText(this, "View ${member.user.username}", Toast.LENGTH_SHORT).show()
                }

                R.id.action_leader_authorize -> {
                    // xử lý unfriend
                    Toast.makeText(this, "Admin authorize ${member.user.username}", Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.action_delete_member -> {
//                    Toast.makeText(this, "Delete ${user.user.username}", Toast.LENGTH_SHORT)
//                        .show()
                    teamId?.let { id ->
//                        projectViewModel.removeMemberFromTeam(id, member.user.userId)
                        AlertDialog.Builder(this)
                            .setTitle("Confirm Delete")
                            .setMessage("Are you sure you want to remove ${member.user.username}?")
                            .setPositiveButton("Yes") { _, _ ->
                                projectViewModel.removeMemberFromTeam(id, member.user.userId)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            }
        }

        binding.recyclerMembers.layoutManager = LinearLayoutManager(this)
        binding.recyclerMembers.adapter = adapter

        teamId?.let{ id ->
            projectViewModel.loadTeamMembers(id)
        }

        projectViewModel.teamMembers.observe(this){result ->
            result.onSuccess { (members, myRole) ->
                adapter.updateData(members)
                adapter.updateCurrentUserRole(myRole)
            }.onFailure { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        projectViewModel.removeMemberResult.observe(this){ result ->
            result.onSuccess {
                Toast.makeText(this, "Member removed successfully", Toast.LENGTH_SHORT).show()
                teamId?.let { id ->
                    projectViewModel.loadTeamMembers(id) // load lại danh sách
                }
            }.onFailure { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSearchFriendDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_friends, null)

        // lấy các view trong dialog
        val etSearch = dialogView.findViewById<EditText>(R.id.etSearchFriend)
        val rvFriends = dialogView.findViewById<RecyclerView>(R.id.rvSearchFriends)

        val adapter = SearchFriendMemberAdapter(emptyList()){ user ->
//            Toast.makeText(this, "Choose ${user.username}", Toast.LENGTH_SHORT).show()
            teamId?.let {
                projectViewModel.addMemberToTeam(it, user.userId)
            } ?: Toast.makeText(this, "Can not found teamId", Toast.LENGTH_SHORT).show()
        }

        rvFriends.layoutManager = LinearLayoutManager(this)
        rvFriends.adapter = adapter

        // tạo dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        userViewModel.searchFriendsResult.observe(this){result ->
            result.onSuccess { users ->
                if (users.isEmpty()) {
                    rvFriends.visibility = View.GONE
                } else {
                    rvFriends.visibility = View.VISIBLE
                    adapter.updateData(users)
                }
            }.onFailure { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // khi gõ text thì cho hiện recyclerview
        etSearch.addTextChangedListener { text ->
            val query = text?.toString()?.trim().orEmpty()
            if (text.isNullOrEmpty()) {
                rvFriends.visibility = View.GONE
            } else {
//                rvFriends.visibility = View.VISIBLE
                // thêm adapter hiển thị kết quả tìm kiếm bạn bè ở đây
                userViewModel.searchFriends(query)
            }
        }

        projectViewModel.addMemberResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Join team successfully", Toast.LENGTH_SHORT).show()
                teamId?.let { id ->
                    projectViewModel.loadTeamMembers(id) // load lại danh sách
                }
            }.onFailure { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchBoxSetUp(){
        binding.searchBox.addTextChangedListener { text ->
            val query = text?.toString()?.trim().orEmpty()
            adapter.filter(query)
        }
    }
}