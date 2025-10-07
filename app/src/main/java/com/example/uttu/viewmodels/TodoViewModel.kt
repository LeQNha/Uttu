package com.example.uttu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.models.Todo
import com.example.uttu.repositories.TodoRepository

class TodoViewModel(val todoRepository: TodoRepository): ViewModel() {

    private val _addTodoResult = MutableLiveData<Result<String>>()
    val addTodoResult: LiveData<Result<String>> = _addTodoResult

    private val _todos = MutableLiveData<Result<List<Todo>>>()
    val todos: LiveData<Result<List<Todo>>> = _todos

    private val _updateTodoStatusResult = MutableLiveData<Result<Void?>>()
    val updateTodoStatusResult: LiveData<Result<Void?>> = _updateTodoStatusResult

    private val _deleteTodoResult = MutableLiveData<Result<Void?>>()
    val deleteTodoResult: LiveData<Result<Void?>> = _deleteTodoResult

    fun addTodo(taskId: String, todo: Todo) {
        todoRepository.addTodo(taskId, todo) { result ->
            _addTodoResult.postValue(result)
        }
    }

    fun getTodos(taskId: String) {
        todoRepository.getTodos(taskId) { result ->
            _todos.postValue(result)
        }
    }

    fun updateTodoStatus(taskId: String, todoId: String, newStatus: Boolean) {
        todoRepository.updateTodoStatus(taskId, todoId, newStatus) { result ->
            _updateTodoStatusResult.postValue(result)
        }
    }

    fun deleteTodo(taskId: String, todoId: String) {
        todoRepository.deleteTodo(taskId, todoId) { result ->
            _deleteTodoResult.postValue(result)
        }
    }

    class TodoViewModelFactory(val todoRepository: TodoRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodoViewModel(todoRepository) as T
        }
    }
}