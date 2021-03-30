package com.example.ess.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.ess.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        bottomNavInit()
    }

    private fun bottomNavInit(){
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        bottomNavView.background = null
        bottomNavView.menu.getItem(2).isEnabled = false
        bottomNavView.setupWithNavController(Navigation.findNavController(this,R.id.bottomNavView))
    }
}