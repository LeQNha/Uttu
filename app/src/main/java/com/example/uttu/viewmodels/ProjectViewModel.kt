package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uttu.models.Project
import com.example.uttu.models.TeamMember
import com.example.uttu.models.User
import com.example.uttu.repositories.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel(val projectRepository: ProjectRepository): ViewModel() {

    private val _createProjectResult = MutableLiveData<Result<Unit>>()
    val createProjectResult: LiveData<Result<Unit>> = _createProjectResult

    private val _userProjects = MutableLiveData<Result<List<Project>>>()
    val userProjects: LiveData<Result<List<Project>>> = _userProjects

    private val _addMemberResult = MutableLiveData<Result<Boolean>>()
    val addMemberResult: LiveData<Result<Boolean>> = _addMemberResult

    private val _removeMemberResult = MutableLiveData<Result<Boolean>>()
    val removeMemberResult: LiveData<Result<Boolean>> = _removeMemberResult

    private val _deleteProjectResult = MutableLiveData<Result<Boolean>>()
    val deleteProjectResult: LiveData<Result<Boolean>> = _deleteProjectResult

//    private val _teamMembers = MutableLiveData<Result<List<TeamMember>>>()
//    val teamMembers: LiveData<Result<List<TeamMember>>> = _teamMembers

    // Trả về cả danh sách thành viên + role hiện tại
    // ✅ Sửa cú pháp: chỉ cần một dấu >
    private val _teamMembers = MutableLiveData<Result<Pair<List<TeamMember>, String>>>()
    val teamMembers: LiveData<Result<Pair<List<TeamMember>, String>>> = _teamMembers

    private val _updateProjectNameResult = MutableLiveData<Result<Unit>>()
    val updateProjectNameResult: LiveData<Result<Unit>> = _updateProjectNameResult

    private val _updateProjectStatusResult = MutableLiveData<Result<Unit>>()
    val updateProjectStatusResult: LiveData<Result<Unit>> = _updateProjectStatusResult

    private val _searchMembersResults = MutableLiveData<List<User>>()
    val searchMembersResults: LiveData<List<User>> = _searchMembersResults


    fun createProject(projectName: String){
        viewModelScope.launch {
            val result = projectRepository.createProject(projectName)
            _createProjectResult.value = result
        }
    }

    fun getUserProjects(){
        viewModelScope.launch {
            val result = projectRepository.getUserProjects()
            _userProjects.value = result
        }
    }

    fun addMemberToTeam(teamId: String, userId: String) {
        viewModelScope.launch {
            val result = projectRepository.addMemberToTeam(teamId, userId)
            _addMemberResult.value = result
        }
    }

//    fun loadTeamMembers(teamId: String) {
//        projectRepository.loadTeamMembers(teamId) { result ->
//            _teamMembers.postValue(result)
//        }
//    }

    fun loadTeamMembers(teamId: String) {
        projectRepository.loadTeamMembers(teamId) { result ->
            result.onSuccess { members ->
                val currentUserId = projectRepository.getCurrentUserId()
                val myRole = members.find { it.user.userId == currentUserId }?.role ?: "Member"
                _teamMembers.postValue(Result.success(Pair(members, myRole)))
            }.onFailure {
                _teamMembers.postValue(Result.failure(it))
            }
        }
    }

    fun removeMemberFromTeam(teamId: String, memberId: String){
        viewModelScope.launch {
            val result = projectRepository.removeMemberFromTeam(teamId, memberId)
            _removeMemberResult.value = result
        }
    }

    fun deleteProject(teamId: String){
        viewModelScope.launch {
            val result = projectRepository.deleteProject(teamId)
            _deleteProjectResult.value = result
        }
    }

    fun updateProjectName(projectId: String, newName: String) {
        projectRepository.updateProjectName(projectId, newName) { result ->
            _updateProjectNameResult.postValue(result)
        }
    }

    fun updateProjectStatus(projectId: String, newStatus: String) {
        viewModelScope.launch {
            projectRepository.updateProjectStatus(projectId, newStatus) { result ->
                _updateProjectStatusResult.postValue(result)
            }
        }
    }

    fun searchMembersInProject(projectId: String, keyword: String) {
        projectRepository.searchMembersInProject(projectId, keyword) { list ->
            _searchMembersResults.postValue(list)
        }
    }

    class ProjectViewModelFactory(val projectRepository: ProjectRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectViewModel(projectRepository) as T
        }
    }
}