package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uttu.models.Project
import com.example.uttu.repositories.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel(val projectRepository: ProjectRepository): ViewModel() {

    private val _createProjectResult = MutableLiveData<Result<Unit>>()
    val createProjectResult: LiveData<Result<Unit>> = _createProjectResult

    private val _userProjects = MutableLiveData<Result<List<Project>>>()
    val userProjects: LiveData<Result<List<Project>>> = _userProjects

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

    class ProjectViewModelFactory(val projectRepository: ProjectRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectViewModel(projectRepository) as T
        }
    }
}