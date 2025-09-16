package com.example.uttu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.repositories.ProjectRepository
import com.example.uttu.repositories.TaskRepository
import com.example.uttu.repositories.TodoRepository
import com.example.uttu.repositories.UserRepository
import com.example.uttu.viewmodels.ProjectViewModel
import com.example.uttu.viewmodels.TaskViewModel
import com.example.uttu.viewmodels.TodoViewModel
import com.example.uttu.viewmodels.UserViewModel

open class BaseActivity : AppCompatActivity() {


    lateinit var userViewModel: UserViewModel
    lateinit var projectViewModel: ProjectViewModel
    lateinit var taskViewModel: TaskViewModel
    lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_base)

        userViewModelSetUp()
        projectViewModelSetUp()
        taskViewModelSetUp()
        todoViewModelSetUp()
    }

    private fun userViewModelSetUp(){
        val userRepository = UserRepository()
        val userFactory = UserViewModel.UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]
    }
    private fun projectViewModelSetUp(){
        val projectRepository = ProjectRepository()
        val projectFactory = ProjectViewModel.ProjectViewModelFactory(projectRepository)
        projectViewModel = ViewModelProvider(this, projectFactory)[ProjectViewModel::class.java]
    }
    private fun taskViewModelSetUp(){
        val taskRepository = TaskRepository()
        val taskFactory = TaskViewModel.TaskViewModelFactory(taskRepository)
        taskViewModel = ViewModelProvider(this, taskFactory)[TaskViewModel::class.java]
    }
    private fun todoViewModelSetUp(){
        val todoRepository = TodoRepository()
        val todoFactory = TodoViewModel.TodoViewModelFactory(todoRepository)
        todoViewModel = ViewModelProvider(this, todoFactory)[TodoViewModel::class.java]
    }
}