package com.example.uttu.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.uttu.R
import com.example.uttu.databinding.FragmentAccountBinding
import com.example.uttu.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_account, container, false)
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClickListenerSetUp()
    }

    private fun onClickListenerSetUp(){
        binding.btnEditProfile.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
            dialog.setContentView(view)

            val etName = view.findViewById<EditText>(R.id.etName)
            val etEmail = view.findViewById<EditText>(R.id.etEmail)
            val etPhone = view.findViewById<EditText>(R.id.etPhone)
            val etAddress = view.findViewById<EditText>(R.id.etAddress)
            val btnSave = view.findViewById<Button>(R.id.btnSaveProfile)

            // Load sẵn dữ liệu từ TextView bên ngoài
            etName.setText(binding.tvUsername.text.toString())
            etEmail.setText(binding.tvEmail.text.toString())
            etPhone.setText(binding.tvPhoneNumber.text.toString())
            etAddress.setText(binding.tvAddress.text.toString())

            btnSave.setOnClickListener {
                // Lưu lại giá trị
                binding.tvUsername.text = etName.text.toString()
                binding.tvEmail.text = etEmail.text.toString()
                binding.tvPhoneNumber.text = etPhone.text.toString()
                binding.tvAddress.text = etAddress.text.toString()
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}