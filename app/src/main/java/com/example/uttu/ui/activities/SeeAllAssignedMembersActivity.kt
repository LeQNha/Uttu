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
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uttu.BaseActivity
import com.example.uttu.R
import com.example.uttu.adapters.AssignedMemberAdapter
import com.example.uttu.adapters.SearchAssignMemberAdapter
import com.example.uttu.adapters.SearchFriendMemberAdapter
import com.example.uttu.databinding.ActivitySeeAllAssignedMembersBinding
import com.example.uttu.models.User

class SeeAllAssignedMembersActivity : BaseActivity() {

    private lateinit var binding: ActivitySeeAllAssignedMembersBinding
    private var taskId: String? = null
    private var projectId: String? = null
    private var currentUserRole: String = "Member" // hoặc "Leader"

    private lateinit var assignedMemberAdapter: AssignedMemberAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_see_all_assigned_members)
        binding = ActivitySeeAllAssignedMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getStringExtra("taskId") // lấy projectId truyền sang
        projectId = intent.getStringExtra("projectId")
        currentUserRole = intent.getStringExtra("userRole") ?: "Member"

        taskId?.let { id ->
            taskViewModel.getAssignedMembersByTaskId(id)
        }

        // chỉ Leader mới được thấy FAB
        if (currentUserRole == "Leader") {
            binding.fabAssignNewMember.show()
        } else {
            binding.fabAssignNewMember.hide()
        }

        onClickListenerSetUp()
        assignMemberRvSetUp()
        searchBoxSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.fabAssignNewMember.setOnClickListener {
            showSearchAssignMemberDialog()
        }
        binding.btnBack.setOnClickListener {
            finish() // Kết thúc activity hiện tại và quay lại activity trước đó
        }
    }

    private fun assignMemberRvSetUp(){
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

//        val adapter = AssignedMemberAdapter(emptyList(), "Leader"){assignedMember, actionId ->
//            when(actionId) {
//
//                R.id.action_unassign_member -> {
////                    Toast.makeText(this, "Delete ${user.user.username}", Toast.LENGTH_SHORT)
////                        .show()
//                    taskId?.let { id ->
////                        projectViewModel.removeMemberFromTeam(id, member.user.userId)
//                        AlertDialog.Builder(this)
//                            .setTitle("Confirm Delete")
//                            .setMessage("Are you sure you want to unassign ${assignedMember.username}?")
//                            .setPositiveButton("Yes") { _, _ ->
//                                Toast.makeText(this, "Unassign ${assignedMember.username}", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//                            .setNegativeButton("Cancel", null)
//                            .show()
//                    }
//                }
//            }
//        }

        taskViewModel.assignedMembers.observe(this) { users ->
            assignedMemberAdapter =
                AssignedMemberAdapter(users, currentUserRole) { assignedMember, actionId ->
                    when (actionId) {
                        R.id.action_unassign_member -> {
                            AlertDialog.Builder(this)
                                .setTitle("Confirm Delete")
                                .setMessage("Are you sure you want to unassign ${assignedMember.username}?")
                                .setPositiveButton("Yes") { _, _ ->
                                    taskId?.let { tId ->
                                        taskViewModel.unassignMember(tId, assignedMember.userId)
                                    }
                                    Toast.makeText(
                                        this,
                                        "Unassign ${assignedMember.username}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // TODO: gọi TaskViewModel.unassignMember(assignedMember.assignedMemberId)
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }
                    }
                }

            binding.recyclerAssignedMembers.layoutManager = LinearLayoutManager(this)
            binding.recyclerAssignedMembers.adapter = assignedMemberAdapter

        }
//        binding.recyclerAssignedMembers.layoutManager = LinearLayoutManager(this)
//        binding.recyclerAssignedMembers.adapter = adapter

        taskViewModel.unassignResult.observe(this) { (success, msg) ->
            if (success) {
                Toast.makeText(this, "Unassigned successfully", Toast.LENGTH_SHORT).show()
                taskId?.let { id -> taskViewModel.getAssignedMembersByTaskId(id) } // load lại
            } else {
                Toast.makeText(this, msg ?: "Unassign failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSearchAssignMemberDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_assign_member, null)

        // lấy các view trong dialog
        val etSearch = dialogView.findViewById<EditText>(R.id.etSearchMember)
        val rvMembers = dialogView.findViewById<RecyclerView>(R.id.rvSearchMembers)

        val adapter = SearchAssignMemberAdapter(emptyList()){ user ->
//            Toast.makeText(this, "Choose ${user.username}", Toast.LENGTH_SHORT).show()
            taskId?.let { tId ->
                taskViewModel.assignMemberToTask(tId, user.userId)
            } ?: Toast.makeText(this, "Can not found taskId", Toast.LENGTH_SHORT).show()
        }

        rvMembers.layoutManager = LinearLayoutManager(this)
        rvMembers.adapter = adapter

        // tạo dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        // khi gõ text thì cho hiện recyclerview
        etSearch.addTextChangedListener { text ->
            val query = text?.toString()?.trim().orEmpty()
            if (text.isNullOrEmpty()) {
                rvMembers.visibility = View.GONE
            } else {
//                rvFriends.visibility = View.VISIBLE
                // thêm adapter hiển thị kết quả tìm kiếm bạn bè ở đây
                rvMembers.visibility = View.VISIBLE
                val id = projectId ?: ""
                android.util.Log.d("DEBUG_SEARCH", "Searching in projectId=$id with query=$query")
                projectViewModel.searchMembersInProject(id, query)
            }
        }

        // Observe LiveData
        projectViewModel.searchMembersResults.observe(this) { users ->
            android.util.Log.d("DEBUG_SEARCH", "Search result size=${users.size}, users=$users")
            adapter.updateData(users)
            rvMembers.visibility = if (users.isEmpty()) View.GONE else View.VISIBLE
        }

        taskViewModel.assignResult.observe(this) { (success, msg) ->
            if (success) {
                Toast.makeText(this, "Assigned successfully", Toast.LENGTH_SHORT).show()
                taskId?.let { id -> taskViewModel.getAssignedMembersByTaskId(id) } // load lại
            } else {
                Toast.makeText(this, msg ?: "Assign failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchBoxSetUp(){
        binding.searchBox.addTextChangedListener { text ->
            val query = text?.toString()?.trim().orEmpty()
            assignedMemberAdapter.filter(query)
        }
    }
}