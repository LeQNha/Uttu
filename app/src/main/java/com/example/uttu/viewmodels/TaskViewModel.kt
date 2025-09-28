package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.models.Task
import com.example.uttu.repositories.TaskRepository

class TaskViewModel(val taskRepository: TaskRepository): ViewModel() {

    private val _addTaskResult = MutableLiveData<Pair<Boolean, String?>>()
    val addTaskResult: LiveData<Pair<Boolean, String?>> = _addTaskResult

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    fun addTask(task: Task) {
        taskRepository.addTask(task) { success, docId ->
            _addTaskResult.postValue(Pair(success, docId))
        }
    }

    fun loadTasks(projectId: String) {
        taskRepository.getTasksByProjectId(projectId) { list ->
            _tasks.postValue(list)
        }
    }

    class TaskViewModelFactory(val taskRepository: TaskRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(taskRepository) as T
        }
    }
}