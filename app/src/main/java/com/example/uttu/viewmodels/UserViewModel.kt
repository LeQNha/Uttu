package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uttu.models.SearchUserResult
import com.example.uttu.models.User
import com.example.uttu.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(val userRepository: UserRepository): ViewModel() {

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _searchResult = MutableLiveData<Result<List<SearchUserResult>>>()
    val searchResult: LiveData<Result<List<SearchUserResult>>> = _searchResult

    private val _sendRequestResult = MutableLiveData<Result<Unit>>()
    val sendRequestResult: LiveData<Result<Unit>> = _sendRequestResult

    private val _cancelRequestResult = MutableLiveData<Result<Unit>>()
    val cancelRequestResult: LiveData<Result<Unit>> = _cancelRequestResult

    private val _incomingFriendRequests = MutableLiveData<Result<List<User>>>()
    val incomingFriendRequests: LiveData<Result<List<User>>> = _incomingFriendRequests

    private val _declineRequestResult = MutableLiveData<Result<String>>() // return senderId khi decline
    val declineRequestResult: LiveData<Result<String>> = _declineRequestResult

    private val _acceptRequestResult = MutableLiveData<Result<String>>()
    val acceptRequestResult: LiveData<Result<String>> = _acceptRequestResult

    private val _friends = MutableLiveData<Result<List<User>>>()
    val friends: LiveData<Result<List<User>>> = _friends

    private val _unfriendResult = MutableLiveData<Result<String>>()
    val unfriendResult: LiveData<Result<String>> = _unfriendResult

    private val _searchFriendsResult = MutableLiveData<Result<List<User>>>()
    val searchFriendsResult: LiveData<Result<List<User>>> = _searchFriendsResult


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

    fun sendFriendRequest(receiverId: String){
        userRepository.sendFriendRequest(receiverId){ success, error ->
            if(success){
                _sendRequestResult.value = Result.success(Unit)
            }else {
                _sendRequestResult.value = Result.failure(Exception(error ?: "Send request failed"))
            }
        }
    }

    fun cancelFriendRequest(receiverId: String){
        userRepository.cancelFriendRequest(receiverId){ success, error ->
            if(success){
                _cancelRequestResult.value = Result.success(Unit)
            }else {
                _cancelRequestResult.value = Result.failure(Exception(error ?: "Cancel request failed"))
            }
        }
    }

    fun getIncomingFriendRequests(){
        userRepository.getIncomingFriendRequests { users, errorMsg ->
            if(users != null){
                _incomingFriendRequests.value = Result.success(users)
            }  else {
                _incomingFriendRequests.value = Result.failure(Exception(errorMsg ?: "Failed to load requests"))
            }
        }
    }

    fun declineFriendRequest(senderId: String) {
        userRepository.declineFriendRequest(senderId) { success, error ->
            if (success) {
                _declineRequestResult.value = Result.success(senderId)
            } else {
                _declineRequestResult.value = Result.failure(Exception(error ?: "Decline failed"))
            }
        }
    }

    fun acceptFriendRequest(senderId: String){
        userRepository.acceptFriendRequests(senderId){ success, error ->
            if(success){
                _acceptRequestResult.value = Result.success(senderId)
            }else{
                _acceptRequestResult.value = Result.failure(Exception(error ?: "Accept failed"))
            }
        }
    }

    fun getFriends(){
        userRepository.getFriends { users, error ->
            if(users != null){
                _friends.value = Result.success(users)
            }
        }
    }

    fun unfriend(friendId: String){
        userRepository.unfriend(friendId){success, error ->
            if(success){
                _unfriendResult.value = Result.success(friendId)
            }else {
                _unfriendResult.value = Result.failure(Exception(error ?: "Failed to unfriend"))
            }
        }
    }

    fun searchFriends(query: String) {
        viewModelScope.launch {
            val result = userRepository.searchFriends(query)
            _searchFriendsResult.value = result
        }
    }

    class UserViewModelFactory(val userRepository: UserRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository) as T
        }
    }
}