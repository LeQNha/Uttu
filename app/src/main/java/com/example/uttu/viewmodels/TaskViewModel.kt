package com.example.uttu.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.repositories.TaskRepository

class TaskViewModel(val taskRepository: TaskRepository): ViewModel() {

    class TaskViewModelFactory(val taskRepository: TaskRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(taskRepository) as T
        }
    }
}