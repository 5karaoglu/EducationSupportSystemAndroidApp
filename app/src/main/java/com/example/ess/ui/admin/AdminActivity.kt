package com.example.ess.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ess.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
    }
}