package com.example.uttu.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.repositories.TodoRepository

class TodoViewModel(val todoRepository: TodoRepository) {

    class TodoViewModelFactory(val todoRepository: TodoRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodoViewModel(todoRepository) as T
        }
    }
}