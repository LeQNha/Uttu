package com.example.uttu.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.repositories.ProjectRepository

class ProjectViewModel(val projectRepository: ProjectRepository) {

    class ProjectViewModelFactory(val projectRepository: ProjectRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProjectViewModel(projectRepository) as T
        }
    }
}