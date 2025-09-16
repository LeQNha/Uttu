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
import com.example.uttu.MainActivity
import com.example.uttu.R
import com.example.uttu.databinding.FragmentLoginBinding
import com.example.uttu.viewmodels.UserViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_login, container, false)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // lấy userViewModel từ AuthentificationActivity (extends BaseActivity)
        userViewModel = (requireActivity() as AuthentificationActivity).userViewModel

        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.btnRegisterNow.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
        binding.authLoginButton.setOnClickListener {
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.loginUser(email, password)
        }
        userViewModel.loginResult.observe(viewLifecycleOwner){ result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Login success!", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // đóng AuthentificationActivity
            }.onFailure {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}