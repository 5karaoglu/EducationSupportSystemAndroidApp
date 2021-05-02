package com.example.ess.util

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Functions {
    companion object{
        fun getCurrentDate(): Long {
            return Date().time
        }
        fun tsToDate(timestamp: String): String{
            var simpleDateFormat = SimpleDateFormat("EEE dd-MM-yyyy hh:mm:ss aa", Locale.getDefault())
            var cal = Calendar.getInstance()
            cal.timeInMillis = timestamp.toLong()
            return simpleDateFormat.format(cal.time)
        }

    }
}