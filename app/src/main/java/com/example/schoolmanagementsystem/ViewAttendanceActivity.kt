package com.example.schoolmanagementsystem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ViewAttendanceActivity.kt
class ViewAttendanceActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var registrationNumber: String
    private lateinit var attendanceListView: ListView
    private lateinit var attendanceSummaryTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attendance)

        db = DatabaseHelper(this)
        registrationNumber = intent.getStringExtra("registration_number") ?: ""
        attendanceListView = findViewById(R.id.attendanceListView)
        attendanceSummaryTextView = findViewById(R.id.attendanceSummaryTextView)

        displayAttendance()
    }

    private fun displayAttendance() {
        val attendanceRecords = db.getAttendanceForStudent(registrationNumber)

        if (attendanceRecords.isEmpty()) {
            attendanceSummaryTextView.text = "No attendance records available."
            return
        }

        val attendanceStrings = attendanceRecords.map {
            val date = it.first
            val status = if (it.second) "Present" else "Absent"
            "$date - $status"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, attendanceStrings)
        attendanceListView.adapter = adapter

        // Calculate and display attendance summary
        val presentCount = attendanceRecords.count { it.second }
        val totalClasses = attendanceRecords.size
        val attendancePercentage = (presentCount / totalClasses.toDouble()) * 100
        attendanceSummaryTextView.text = "Attendance: $presentCount/$totalClasses (${String.format("%.2f", attendancePercentage)}%)"
    }
}
