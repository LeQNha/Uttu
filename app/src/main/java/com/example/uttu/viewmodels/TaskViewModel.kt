package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uttu.models.Task
import com.example.uttu.models.User
import com.example.uttu.repositories.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(val taskRepository: TaskRepository): ViewModel() {

    private val _addTaskResult = MutableLiveData<Pair<Boolean, String?>>()
    val addTaskResult: LiveData<Pair<Boolean, String?>> = _addTaskResult

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _assignResult = MutableLiveData<Pair<Boolean, String?>>()
    val assignResult: LiveData<Pair<Boolean, String?>> = _assignResult

    private val _assignedMembers = MutableLiveData<List<User>>()
    val assignedMembers: LiveData<List<User>> = _assignedMembers

    private val _unassignResult = MutableLiveData<Pair<Boolean, String?>>()
    val unassignResult: LiveData<Pair<Boolean, String?>> = _unassignResult

    private val _updateTaskStatusResult = MutableLiveData<Result<Unit>>()
    val updateTaskStatusResult: LiveData<Result<Unit>> = _updateTaskStatusResult


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

    fun assignMemberToTask(taskId: String, memberId: String) {
        taskRepository.assignMemberToTask(taskId, memberId) { success, msg ->
            _assignResult.postValue(Pair(success, msg))
        }
    }

    fun getAssignedMembersByTaskId(taskId: String) {
        taskRepository.getAssignedMembersByTaskId(taskId) { users ->
            _assignedMembers.postValue(users)
        }
    }

    fun unassignMember(taskId: String, memberId: String) {
        taskRepository.unassignMember(taskId, memberId) { success, msg ->
            _unassignResult.postValue(Pair(success, msg))
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        taskRepository.updateTaskStatus(taskId, newStatus) { result ->
            _updateTaskStatusResult.postValue(result)
        }
    }

    class TaskViewModelFactory(val taskRepository: TaskRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(taskRepository) as T
        }
    }
}