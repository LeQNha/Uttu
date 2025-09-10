package com.example.uttu.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.repositories.UserRepository

class UserViewModel(val userRepository: UserRepository) {

    class UserViewModelFactory(val userRepository: UserRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository) as T
        }
    }
}