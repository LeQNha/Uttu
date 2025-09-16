package com.example.uttu.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttu.R
import com.example.uttu.adapters.MemberAdapter
import com.example.uttu.databinding.ActivitySeeAllMembersBinding
import com.example.uttu.models.User

class SeeAllMembersActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeAllMembersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_see_all_members)
        binding = ActivitySeeAllMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memberRvSetUp()
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

        val adapter = MemberAdapter(members){ user, actionId ->
            when(actionId) {
                R.id.action_view_member_profile -> {
                    Toast.makeText(this, "View ${user.username}", Toast.LENGTH_SHORT).show()
                }

                R.id.action_admin_authorize -> {
                    // xử lý unfriend
                    Toast.makeText(this, "Admin authorize ${user.username}", Toast.LENGTH_SHORT)
                        .show()
                }

                R.id.action_delete_member -> {
                    Toast.makeText(this, "Delete ${user.username}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.recyclerMembers.layoutManager = LinearLayoutManager(this)
        binding.recyclerMembers.adapter = adapter
    }
}