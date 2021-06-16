package com.example.ess.ui.student

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ess.EssApplication
import com.example.ess.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        bottomNavInit()
        (application as EssApplication).userType = "Student"
    }

    private fun bottomNavInit() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavStudent)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentStudent) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.background = null
        bottomNavView.setupWithNavController(navController)
    }
}