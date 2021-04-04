package com.example.ess.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ess.R
import com.example.ess.ui.admin.AdminActivity
import com.example.ess.ui.admin.AdminRepository
import com.example.ess.ui.student.StudentActivity
import com.example.ess.ui.teacher.TeacherActivity
import com.example.ess.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val TAG = "Login Activity"
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loadingScreen = findViewById<RelativeLayout>(R.id.blurScreen)

        viewModel.dataState.observe(this, {
            when (it) {
                is DataState.Success -> {
                    Toast.makeText(this, "Authorization Success !", Toast.LENGTH_SHORT).show()
                    loadingScreen.visibility = View.GONE
                    when(it.data){
                        "Student" -> {
                            val intent = Intent(this, StudentActivity::class.java)
                            startActivity(intent)
                        }
                        "Teacher" -> {
                            val intent = Intent(this, TeacherActivity::class.java)
                            startActivity(intent)
                        }
                        "Admin" -> {
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        }
                    }

                }
                is DataState.Error -> {
                    Toast.makeText(this, "Error: ${it.throwable.message}", Toast.LENGTH_SHORT)
                            .show()
                    loadingScreen.visibility = View.GONE
                }
                is DataState.Loading -> {
                    Log.d(TAG, "onCreate: loading")
                    loadingScreen.visibility = View.VISIBLE
                }
            }
        })

        val button = findViewById<Button>(R.id.buttonLogin)
        val email = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        button.setOnClickListener {
            Log.d(TAG, "loginbutton clicked")
            viewModel.signIn(email.text.toString(),password.text.toString())
        }
    }


}