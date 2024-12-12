package com.example.schoolmanagementsystem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ViewTimetableActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var timetableListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_timetable)

        db = DatabaseHelper(this)
        timetableListView = findViewById(R.id.timetableListView)

        // Retrieve class and section from Intent
        val studentClass = intent.getStringExtra("student_class") ?: ""
        val studentSection = intent.getStringExtra("student_section") ?: ""

        // Fetch timetable for the specific class and section
        val timetable = db.getTimetableForStudent(studentClass, studentSection)

        // Set up ListView adapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            timetable.map { "${it.first}: ${it.second}" }
        )
        timetableListView.adapter = adapter
    }
}
