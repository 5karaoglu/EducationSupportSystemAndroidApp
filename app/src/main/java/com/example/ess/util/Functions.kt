package com.example.ess.util

import java.util.*

class Functions {
    companion object{
        fun getCurrentDate(): Long {
            return Date().time
        }

        fun getUserType(type: Int):String {
            return when(type){
                0 -> "Student"
                1 -> "Teacher"
                2 -> "Admin"
                else -> "Student"
            }
        }

    }
}