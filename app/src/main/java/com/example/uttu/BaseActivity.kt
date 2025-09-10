package com.example.uttu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.uttu.repositories.ProjectRepository
import com.example.uttu.viewmodels.ProjectViewModel

class BaseActivity : AppCompatActivity() {

    protected lateinit var projectViewModel: ProjectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    private fun projectViewModelSetUp(){
        val projectRepository = ProjectRepository()
        val projectFactory = ProjectViewModel.ProjectViewModelFactory(projectRepository)
        projectViewModel = ViewModelProvider(this, projectFactory)[ProjectViewModel::class.java]
    }
}