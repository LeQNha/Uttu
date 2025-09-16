package com.example.uttu.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.uttu.AuthentificationActivity
import com.example.uttu.BaseActivity
import com.example.uttu.MainActivity
import com.example.uttu.R
import com.example.uttu.databinding.FragmentSignupBinding
import com.example.uttu.viewmodels.UserViewModel

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_signup, container, false)
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = (requireActivity() as AuthentificationActivity).userViewModel
        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.btnLoginNow.setOnClickListener {
            it.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        binding.authRegisterButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Password not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.registerUser(email, password, username)
        }

        userViewModel.registerResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Register success!", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}