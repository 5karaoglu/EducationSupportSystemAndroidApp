package com.example.ess.util

import android.text.SpannableStringBuilder
import androidx.core.text.bold
import androidx.core.text.italic
import com.example.ess.model.ActivityItem
import com.example.ess.model.UserProfile
import java.text.SimpleDateFormat
import java.util.*

class Functions {
    companion object {
        fun getCurrentDate(): Long {
            return Date().time
        }

        fun tsToDate(timestamp: String): String {
            val simpleDateFormat = SimpleDateFormat("EEE dd-MM-yyyy hh:mm:ss aa", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp.toLong()
            return simpleDateFormat.format(cal.time)
        }

        fun tsToHm(timestamp: String): String {
            val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp.toLong()
            return simpleDateFormat.format(cal.time)
        }

        fun isTimesUp(timestamp: String): Boolean {
            val dayAsTimestamp = 86400000
            val dif = timestamp.toLong() - getCurrentDate()
            return dif <= dayAsTimestamp
        }

        fun activityType(activityItem: ActivityItem, user: UserProfile): SpannableStringBuilder {
            var s = SpannableStringBuilder()
            when (activityItem.type) {
                "submission" -> {
                    s = SpannableStringBuilder()
                            .bold { append(user.name) }
                            .append(" submitted ")
                            .italic { append('"').append(activityItem.job).append('"') }
                            .append(" to ")
                            .bold {
                                append(activityItem.issueName +
                                        "/" +
                                        activityItem.className)
                            }
                }
                "comment" -> {
                    s = SpannableStringBuilder()
                            .bold { append(user.name) }
                            .append(" commented ")
                            .italic { append('"').append(activityItem.job).append('"') }
                            .append(" to ")
                            .bold {
                                append(activityItem.issueName +
                                        "/" +
                                        activityItem.className)
                            }
                }
                "rate" -> {
                    s = SpannableStringBuilder()
                            .bold { append(user.name) }
                            .append(" getting rated ")
                            .italic { append('"').append(activityItem.job).append('"') }
                            .append(" from ")
                            .bold {
                                append(activityItem.issueName +
                                        "/" +
                                        activityItem.className)
                            }
                }
            }
            return s
        }
    }
}