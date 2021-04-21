package com.example.ess.ui.teacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ess.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        bottomNavInit()
    }

    private fun bottomNavInit(){
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavTeacher)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentTeacher) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.background = null
        bottomNavView.setupWithNavController(navController)
    }
}