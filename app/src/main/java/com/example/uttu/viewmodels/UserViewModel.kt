package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.models.User
import com.example.uttu.repositories.UserRepository

class UserViewModel(val userRepository: UserRepository): ViewModel() {

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _searchResult = MutableLiveData<Result<List<User>>>()
    val searchResult: LiveData<Result<List<User>>> = _searchResult

    fun registerUser(email: String, password: String, username: String){
        userRepository.registerUser(email, password, username){ success, errorMsg ->
            if(success){
                _registerResult.value = Result.success(Unit)
            }else{
                _registerResult.value = Result.failure(Exception(errorMsg ?: "Register failed"))
            }

        }
    }

    fun loginUser(email: String, password: String){
        userRepository.loginUser(email, password){ task ->
            if(task.isSuccessful){
                _loginResult.postValue(Result.success(Unit))
            }else{
                _loginResult.postValue(Result.failure(task.exception ?: Exception("Login failed")))
            }
        }
    }

    fun searchUsers(query: String){
        userRepository.searchUsers(query){ users, errorMsg ->
            if(users != null){
                _searchResult.value = Result.success(users)
            } else {
                _searchResult.value = Result.failure(Exception(errorMsg ?: "Search failed"))
            }
        }
    }

    class UserViewModelFactory(val userRepository: UserRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository) as T
        }
    }
}