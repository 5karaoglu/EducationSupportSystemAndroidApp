package com.example.ess.ui.teacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ess.EssApplication
import com.example.ess.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class TeacherActivity : AppCompatActivity() {

    private val viewModel: TeacherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        bottomNavInit()
        (application as EssApplication).userType = "Teacher"
    }

    private fun bottomNavInit(){
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentTeacher) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.background = null
        bottomNavView.setupWithNavController(navController)
    }
}